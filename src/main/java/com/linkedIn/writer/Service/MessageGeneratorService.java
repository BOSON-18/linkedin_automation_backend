package com.linkedIn.writer.Service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedIn.writer.Model.MessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Objects;

@Service
public class MessageGeneratorService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public MessageGeneratorService(WebClient.Builder webClient) {
        this.webClient = WebClient.builder().build();
    }

    public String generateMessageReply(MessageRequest messageRequest) {
        //build the prompt

        String prompt = buildPrompt(messageRequest);
        //Craft a request-> We need to follow the structure of REST api call

        // ---------    Example Request -----------

//        {
//            "contents": [{
//            "parts":[{"text": "Explain how AI works"}]
//        }]
//        }


        // --------- Request Format End Of Gemini -------
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of(
                                        "text", prompt
                                )
                        })
                }
        );
        //Do request and Get Response
        // need url and api key

        String response = webClient.post().uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody).retrieve()
                .bodyToMono(String.class).block();


        //Return extracted response
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);//convert json structure to tree structure and then we can ncviaget through entire tree
            return rootNode.path("candidates").get(0)
                    .path("content").path("parts")
                    .get(0).path("text").asText();
        } catch (Exception error) {
            return "Error proccessing request: " + error.getMessage();
        }
    }

    private String buildPrompt(MessageRequest messageRequest) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("Generate a professional LinkedIn message reply as an Indian human for the following message content.Please don't add the subject in the reply and remember to keep it as human writing.");

        if (messageRequest.getTone() != null && !messageRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(messageRequest.getTone()).append(" tone.");
        }
        prompt.append("\nOriginal message: \n").append(messageRequest.getMessageContent());

        return prompt.toString();
    }

}
