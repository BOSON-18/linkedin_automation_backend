package com.linkedIn.writer.Controller;


import com.linkedIn.writer.Model.MessageRequest;
import com.linkedIn.writer.Service.MessageGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/linkedIn")
@CrossOrigin(origins = "http://localhost:5174/")
public class MessageGeneratorController {

    private final MessageGeneratorService service;

        @PostMapping("/generate")
    public ResponseEntity<String> generateMessage(@RequestBody MessageRequest messageRequest){
        String response = service.generateMessageReply(messageRequest);
        return ResponseEntity.ok(response);
    }
}
