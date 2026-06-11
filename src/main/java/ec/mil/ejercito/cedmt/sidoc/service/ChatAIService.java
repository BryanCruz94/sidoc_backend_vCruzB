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

    @Autowired
    public ChatAIService(OpenAIConfig openAIConfig ) {
        this.openAIConfig = openAIConfig;
        this.restTemplate = new RestTemplate();
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
