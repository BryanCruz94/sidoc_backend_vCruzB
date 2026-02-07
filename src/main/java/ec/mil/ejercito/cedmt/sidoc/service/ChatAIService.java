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

    // Modelo rápido/barato SOLO para validación (clasificación)
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
                respuesta = "No puedo responder tu pregunta, soy un chatbot bibliotecario. ¿Te puedo ayudar en algo más?";
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

        /**
         * Validación:
         * - "1" si está relacionado con manuales/nota/reglamentos/instructivos/documentos/búsqueda bibliográfica
         * - "0" si no tiene relación
         * - si es saludo/agradecimiento/despedida: responder como un chatbot humano (NO militar)
         *
         * Reglas importantes:
         * - Devuelve SOLO: "1" o "0" o el texto del saludo/agradecimiento
         * - En saludos: NO responder "Con gusto"
         */
        String systemPrompt = """
                Eres CEDiño, un chatbot bibliotecario.

                Clasifica el mensaje del usuario y responde SOLO con UNA de estas salidas:
                - Responde exactamente "1" si el mensaje solicita o menciona manuales, notas de aula, reglamentos, instructivos, documentos, biblioteca, bibliografía, recomendaciones de material, o búsqueda de documentos.
                - Responde exactamente "0" si el mensaje NO tiene relación con biblioteca/documentación.
                - Si es un saludo, agradecimiento o despedida, responde como un chatbot humano y amable (1 sola línea). 
                  En saludos NO uses "Con gusto". (Ejemplos de saludo: "¡Hola! ¿En qué puedo ayudarte?", "Hola, ¿qué necesitas consultar?", "¡Buenas! ¿Qué buscas hoy?").
                  Para agradecimientos sí puedes responder: "¡De nada! ¿Te ayudo con algo más?", "Con gusto, ¿algo adicional?".
                
                Devuelve solo la respuesta, sin explicaciones.
                """;

        // Usar el modelo rápido para evitar gastar tokens en validación
        String result = sendChatRequest(userMessage, VALIDATION_MODEL, systemPrompt, apiUrl, apiKey);

        // Normalización suave para evitar que te rompa el switch (por si llega "1." / "0.")
        if (result != null) {
            String r = result.trim();
            if (r.startsWith("1")) return "1";
            if (r.startsWith("0")) return "0";
            return r;
        }
        return "¡Hola! ¿En qué puedo ayudarte?";
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

        // Prompt mejorado SOLO por estilo/tono (manteniendo tus reglas de precisión)
        String systemPrompt = """
            Tu nombre es CEDiño. Eres un bibliotecario experto en la documentación del COMANDO DE EDUCACIÓN Y DOCTRINA MILITAR TERRESTRE DEL EJÉRCITO DEL ECUADOR.
            Tu tarea es proporcionar información precisa y útil sobre los manuales, notas de aula y reglamentos publicados, asegurando respuestas claras y bien estructuradas.

            **Instrucciones estrictas:**
            1 Analiza la consulta del usuario y selecciona los manuales más relevantes con base en coincidencias en el nombre, categoría, subcategoría y descripción.
            2 Si te solicitan la cantidad de documentos disponibles, responde primero con el número exacto y luego enlista los manuales.
            3 (NUNCA inventes nombres de manuales). Solo menciona los manuales que aparecen en la lista proporcionada.
            4 Excluye información fuera del contexto de los manuales.

            **Estilo y tono (obligatorio):**
            5 Responde con tono militar ecuatoriano, subordinado, amable y humano.
              - NO menciones rangos/grados del usuario (nunca: soldado/cabo/teniente/coronel/general).
              - NO uses "A sus órdenes".
              - Evita sonar frío: redacta como una persona (una línea breve de cortesía al inicio).
              - Varía la cortesía para que no repita siempre lo mismo. Ejemplos válidos: "Su orden.", "Listo.", "Entendido.", "A la orden.", "Cumplida su orden."
              - No siempre uses cortesía: úsala solo cuando encaje (máximo una por respuesta).

            6 Formato de recomendación (obligatorio):
               - [Nombre del Manual] (Año de publicación): Breve descripción relevante (10-15 palabras).

            7 Si no encuentras coincidencias claras, no inventes: ofrece 2-4 opciones cercanas y haz 1-2 preguntas cortas para precisar.

            🔹 **Lista de manuales, reglamentos y notas de aula en JSON:**
            """ + jsonContext;

        String apiUrl = getApiUrl(provider);
        String apiKey = getApiKey(provider);

        return sendChatRequest(userMessage, model, systemPrompt, apiUrl, apiKey);
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

    //PARA OPENAI (se mantiene como estaba: SIN parámetros extra como max_tokens)
    private String sendChatRequest(String userMessage, String model, String systemPrompt, String apiUrl, String apiKey) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userMessage)
            ));

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
        List<ManualsToChatDTO> manuals = manualRepository.findManualsToChat();
        return manuals;
    }

    /********************************************************************************************************************
     Sección para generar resúmene
     ********************************************************************************************************************/

    //Metodo para obtener el resumen de un manual
    public String getManualAbstract(String textoManual) {
        try {
            // Construcción del prompt optimizado
            String systemPrompt = """
                        Eres un bibliotecario encargado de generar descripciones para Manuales, Notas de aula, Reglamentos, Libros y textos.
                        Analiza el texto proporcionado y elabora un resumen conciso y preciso que sirva como guía del documento.
                        El resumen debe tener entre 1000 y 1200 caracteres, evitando incluir detalles irrelevantes como códigos, nombres, fechas específicas, o contexto histórico
                        No incluyas en el resumen el nombre del libro, manual, nota de aula. Tampoco su código ni fecha de publicación.
                    """;

            // Construcción del JSON dinámicamente
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", textoManual)
            ));

            String requestBody = objectMapper.writeValueAsString(body);

            // Configuración de headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAIConfig.getApiKey2());
            headers.set("Content-Type", "application/json");

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // Realiza la solicitud
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    OPENAI_API_URL, HttpMethod.POST, request, JsonNode.class);

            // Procesa la respuesta
            JsonNode responseBody = response.getBody();
            if (responseBody != null && responseBody.has("choices") &&
                    responseBody.get("choices").size() > 0) {
                return responseBody.get("choices").get(0).get("message").get("content").asText().trim();
            } else {
                // Log para inspección si la respuesta no es válida
                System.err.println("Respuesta inválida recibida: " + responseBody);
                return "No se recibió una respuesta válida del modelo.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ocurrió un error al procesar la solicitud: " + e.getMessage();
        }
    }
}
