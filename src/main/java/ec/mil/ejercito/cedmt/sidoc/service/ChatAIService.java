package ec.mil.ejercito.cedmt.sidoc.service;

import ec.mil.ejercito.cedmt.sidoc.config.OpenAIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;

import ec.mil.ejercito.cedmt.sidoc.dto.ManualsToChatDTO;
import ec.mil.ejercito.cedmt.sidoc.repository.ManualRepository;
import ec.mil.ejercito.cedmt.sidoc.repository.PreguntaChatRepository;
import ec.mil.ejercito.cedmt.sidoc.model.PreguntaChat;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatAIService {

    @Value("${openai.models}")
    private String model;

    @Autowired
    private PreguntaChatRepository preguntaChatRepository;

    @Autowired
    private ManualRepository manualRepository;

    private final OpenAIConfig openAIConfig;
    private final RestTemplate restTemplate;

    private Integer countTokens = 0;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";


    private static final String VALIDATION_MODEL = "gpt-4o-mini";

    @Autowired
    public ChatAIService(OpenAIConfig openAIConfig ) {
        this.openAIConfig = openAIConfig;
        this.restTemplate = new RestTemplate();
    }

    public String chatBot(String userMessage, String model, String provider) {
        Instant inicio = Instant.now();

        // Validación de la pregunta y obtención del tipo de respuesta
        String messageType = validateQuestion(userMessage, provider, model);

        // Creación del objeto de pregunta
        PreguntaChat preguntaChat = new PreguntaChat();
        preguntaChat.setPregunta(userMessage);
        preguntaChat.setApiKey(provider);

        String respuesta;
        switch (messageType) {
            case "1":
                preguntaChat.setTipoPregunta("Pregunta Válida");
                respuesta = getChatResponse(userMessage, model, provider);
                break;
            case "0":
                preguntaChat.setTipoPregunta("Pregunta No Válida");
                respuesta = "No puedo atender esa consulta desde biblioteca. Si desea, indíqueme el tema y le recomiendo manuales o notas de aula.";
                break;
            default:
                preguntaChat.setTipoPregunta("Saludo o Agradecimiento");
                respuesta = messageType;
                break;
        }

        // Calcular el tiempo de respuesta
        Instant fin = Instant.now();
        int tiempoEnCentisegundos = (int) (java.time.Duration.between(inicio, fin).toMillis() / 10);
        preguntaChat.setTiempoRespuesta(tiempoEnCentisegundos);
        preguntaChat.setCantTokens(this.countTokens);
        this.countTokens = 0;
        preguntaChatRepository.save(preguntaChat);

        return respuesta;
    }

    private String validateQuestion(String userMessage, String provider, String model) {
        String apiUrl = getApiUrl(provider);
        String apiKey = getApiKey(provider);

        String systemPrompt = """
                Eres CEDiño, asistente bibliotecario del Comando de Educación y Doctrina Militar Terrestre del Ejército del Ecuador.

                Clasifica el mensaje del usuario en UNA de estas salidas (sin explicación adicional):
                - Responde EXACTAMENTE "1" si el mensaje solicita o menciona manuales, notas de aula, reglamentos, instructivos, documentos, bibliografía, recomendaciones de material o búsqueda de documentos.
                - Responde EXACTAMENTE "0" si el mensaje NO tiene relación con biblioteca/documentación.
                - Si es un saludo o agradecimiento (o despedida), responde con UNA SOLA línea cordial y humana, estilo militar ecuatoriano subordinado.

                Reglas de estilo para saludos/agradecimientos:
                - NO menciones rangos o grados (nunca: soldado/cabo/teniente/coronel/general).
                - NO uses la frase "A sus órdenes".
                - Varía las expresiones (no repitas siempre la misma). Ejemplos permitidos: "Su orden.", "Listo.", "Cumplida su orden.", "A la orden.", "Entendido.", "Con gusto."
                - Manténlo breve (máximo 12 palabras).
                """;
        String raw = sendChatRequest(userMessage, VALIDATION_MODEL, systemPrompt, apiUrl, apiKey,
                0.2, // temperature baja para consistencia en 1/0
                0.0, // frequency_penalty
                0.0, // presence_penalty
                30   // max_tokens mínimo
        );

        return normalizeClassifierOutput(raw);
    }

    private String getChatResponse(String userMessage, String model, String provider) {
        List<ManualsToChatDTO> manuals = getManuals();

        // Convertir la lista de manuales a JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContext;

        try {
            jsonContext = objectMapper.writeValueAsString(manuals);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir manuales a JSON", e);
        }

        String systemPrompt = """
            Tu nombre es CEDiño. Eres un asistente bibliotecario experto en la documentación del COMANDO DE EDUCACIÓN Y DOCTRINA MILITAR TERRESTRE DEL EJÉRCITO DEL ECUADOR.

            Objetivo:
            Dar recomendaciones de manuales, notas de aula, reglamentos e instructivos usando ÚNICAMENTE la lista JSON proporcionada.

            Instrucciones estrictas (precisión):
            1) NUNCA inventes nombres de documentos. Solo menciona elementos que estén en la lista JSON.
            2) Prioriza coincidencias por: nombre, categoría, subcategoría y descripción. Si no hay coincidencia clara, ofrece 2-4 opciones cercanas y formula 1-2 preguntas cortas para уточar.
            3) Si el usuario solicita “cuántos documentos hay” o “cantidad”, responde primero con el número exacto y luego lista.
            4) Mantén la respuesta enfocada en recomendaciones; evita relleno o explicaciones fuera del contexto.

            Estilo (humano + militar ecuatoriano subordinado):
            - No menciones rangos/grados del usuario (nunca: soldado/cabo/teniente/coronel/general).
            - No uses "A sus órdenes".
            - Puedes iniciar con UNA sola frase breve y variable (no siempre la misma). Ejemplos permitidos: "Su orden.", "Listo.", "Cumplida su orden.", "A la orden.", "Entendido.", "Con gusto."
            - No repitas la misma frase de cortesía en todas las respuestas; usa variación natural.
            - Tono respetuoso, cercano y útil.

            Formato de salida:
            - Empieza con 0–1 línea de cortesía (opcional).
            - Luego lista 3 a 6 recomendaciones (si aplica), cada una en este formato exacto:
              - [Nombre del Manual] (Año de publicación): Breve descripción relevante (10-15 palabras).
            - Cierra con una pregunta breve para afinar la búsqueda (si aplica).

            Datos disponibles en cada documento:
            nombre, categoria, subcategoria, descripcion, anioPublicacion

            🔹 Lista de manuales, reglamentos y notas de aula en JSON:
            """ + jsonContext;

        String apiUrl = getApiUrl(provider);
        String apiKey = getApiKey(provider);


        return sendChatRequest(userMessage, model, systemPrompt, apiUrl, apiKey,
                0.6,  // temperature
                0.45, // frequency_penalty
                0.2,  // presence_penalty
                500   // max_tokens
        );
    }

    private String getApiUrl(String provider) {
        switch (provider) {
            case "OpenAI":
                return "https://api.openai.com/v1/chat/completions";
            default:
                throw new IllegalArgumentException("Proveedor no soportado: " + provider);
        }
    }

    private String getApiKey(String provider) {
        switch (provider) {
            case "OpenAI":
                return openAIConfig.getApiKey();
            default:
                throw new IllegalArgumentException("Proveedor no soportado: " + provider);
        }
    }


    private String normalizeClassifierOutput(String raw) {
        if (raw == null) return "Listo.";
        String s = raw.trim();

        // Quitar comillas si el modelo devolvió "1" o '1'
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1).trim();
        }

        if (s.startsWith("1")) return "1";
        if (s.startsWith("0")) return "0";

        // Caso saludo/agradecimiento
        return s.isEmpty() ? "Listo." : s;
    }

    //PARA OPENAI (con parámetros de estilo/longitud)
    private String sendChatRequest(String userMessage,
                                   String model,
                                   String systemPrompt,
                                   String apiUrl,
                                   String apiKey,
                                   double temperature,
                                   double frequencyPenalty,
                                   double presencePenalty,
                                   int maxTokens) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userMessage)
            ));

            // Parámetros para tono humano (y evitar repetición)
            body.put("temperature", temperature);
            body.put("frequency_penalty", frequencyPenalty);
            body.put("presence_penalty", presencePenalty);
            body.put("max_tokens", maxTokens);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, JsonNode.class);

            JsonNode responseBody = response.getBody();

            // Variable para almacenar totalTokenCount
            int totalTokens = 0;

            if (responseBody != null && responseBody.has("usage")) {
                JsonNode usage = responseBody.get("usage");
                if (usage.has("total_tokens")) {
                    totalTokens = usage.get("total_tokens").asInt();
                }
            }

            // Guardar el conteo de tokens en la variable de instancia
            this.countTokens = totalTokens;
            System.out.println("Total Tokens: " + this.countTokens);

            return responseBody != null && responseBody.has("choices")
                    ? responseBody.get("choices").get(0).get("message").get("content").asText().trim()
                    : "Error en la respuesta del modelo.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar la solicitud: " + e.getMessage();
        }
    }



    //Servicio para obtener los manuales
    public List<ManualsToChatDTO> getManuals() {
        return manualRepository.findManualsToChat();
    }

    /********************************************************************************************************************
     Sección para generar resúmenes
     ********************************************************************************************************************/

    //Metodo para obtener el resumen de un manual
    public String getManualAbstract(String textoManual) {
        try {
            // Prompt ajustado para producir texto más fluido, sin datos irrelevantes
            String systemPrompt = """
                        Eres un bibliotecario encargado de generar descripciones para Manuales, Notas de aula, Reglamentos, Libros y textos.
                        Analiza el texto proporcionado y elabora un resumen conciso y preciso que sirva como guía del documento.

                        Reglas:
                        - Longitud: entre 1000 y 1200 caracteres.
                        - Evita detalles irrelevantes como códigos, nombres propios, fechas específicas o contexto histórico.
                        - No incluyas el nombre del documento, ni su código, ni su año de publicación.
                        - Redacción clara y natural.
                    """;

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", textoManual)
            ));

            // Parámetros: resumen consistente, no “poético”
            body.put("temperature", 0.4);
            body.put("frequency_penalty", 0.2);
            body.put("presence_penalty", 0.1);
            body.put("max_tokens", 600);

            String requestBody = objectMapper.writeValueAsString(body);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAIConfig.getApiKey2());
            headers.set("Content-Type", "application/json");

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    OPENAI_API_URL, HttpMethod.POST, request, JsonNode.class);

            JsonNode responseBody = response.getBody();
            if (responseBody != null && responseBody.has("choices") &&
                    responseBody.get("choices").size() > 0) {
                return responseBody.get("choices").get(0).get("message").get("content").asText().trim();
            } else {
                System.err.println("Respuesta inválida recibida: " + responseBody);
                return "No se recibió una respuesta válida del modelo.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ocurrió un error al procesar la solicitud: " + e.getMessage();
        }
    }
}
