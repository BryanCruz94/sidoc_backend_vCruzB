package ec.mil.ejercito.cedmt.sidoc.controller;

import ec.mil.ejercito.cedmt.sidoc.service.ChatAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ec.mil.ejercito.cedmt.sidoc.dto.ManualsToChatDTO;

import java.util.List;

@RestController
@RequestMapping("/cedmt/sidoc/api/chat")
public class ChatController {

    @Autowired
    private ChatAIService chatAIService;

    @Value("${openai.models}")
    private String model;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/manualsQuestions")
    public String chatOpenAI(@RequestBody String userMessage)
    {
        return chatAIService.chatBot(userMessage, model, "OpenAI");
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/list")
    public List<ManualsToChatDTO> getActiveManuals() {
        return chatAIService.getManuals();
    }
}