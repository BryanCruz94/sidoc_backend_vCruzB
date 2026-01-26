package ec.mil.ejercito.cedmt.sidoc.service;

import ec.mil.ejercito.cedmt.sidoc.config.OpenAIConfig;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private PreguntaChatRepository preguntaChatRepository;

    @Autowired
    private ManualRepository manualRepository;

    private final OpenAIConfig openAIConfig;
    private final RestTemplate restTemplate;

    private Integer countTokens = 0;


    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";


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

        String systemPrompt = """
                Eres un chatbot bibliotecario especializado en manuales, libros y reglamentos. 
                Clasifica cada mensaje según las siguientes reglas:
                1. Si la pregunta está relacionada con libros o manuales responde con '1'.
                2. Si no tiene relación alguna, responde con '0'.
                3. Si es un saludo o agradecimiento, responde educadamente.
                Devuelve solo la respuesta sin explicaciones adicionales.
                """;

        return sendChatRequest(userMessage, model, systemPrompt, apiUrl, apiKey);


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

        // Crear el prompt con el JSON
        String systemPrompt = """
            Eres un bibliotecario experto en la documentación del COMANDO DE EDUCACIÓN Y DOCTRINA MILITAR TERRESTRE DEL EJÉRCITO DEL ECUADOR. 
            Tu tarea es proporcionar información precisa y útil sobre los manuales publicados, asegurando respuestas formales y bien estructuradas. 

             **Instrucciones estrictas:**  
            1️ Analiza rigurosamente la consulta del usuario y selecciona los manuales más relevantes con base en coincidencias exactas en el nombre, categoría y descripción.  
            2 Si te solicitan la cantidad de documentos disponibles, responde primero con el número exacto y luego enlista los manuales.  
            3 (NUNCA inventes nombres de manuales). Solo menciona los manuales que aparecen en la lista proporcionada.  
            4 Excluye explicaciones innecesarias o información fuera del contexto de los manuales.  
            5 Tus respuestas deben ser claras y estructuradas en un formato de recomendación, como este ejemplo:  

               - [Nombre del Manual] (Año de publicación): Breve descripción relevante (10-15 palabras).  

            🔹 **Lista de manuales en JSON:**  
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


   //PARA OPENAI
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
            body.put("model", "gpt-4o-mini");
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
