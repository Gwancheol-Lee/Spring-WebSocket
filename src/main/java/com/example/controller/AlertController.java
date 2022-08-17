package com.example.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.model.AlertMessage;
import com.example.model.ChatMessage;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8080"}, allowedHeaders = "*")
public class AlertController {
	
	private final SimpMessagingTemplate template;
	
	@MessageMapping("/pub/alert/{userId}")
    public void sendAlert(@DestinationVariable String userId, @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		System.out.println("userId: " + userId);
		headerAccessor.getSessionAttributes().put("userid", userId);
		System.out.println("sendMessage: " + chatMessage);
		template.convertAndSend("/sub/chat/" + userId, chatMessage);
    }
	
    @MessageMapping("/pub/alert")
    @SendTo("/sub/alert")
    public AlertMessage addUser(@Payload AlertMessage event, SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username", event.getSender());
        Map<String, String> result = new LinkedHashMap<>();
        
        //result.put("event", event);
        System.out.println("alert.sendEvent: " + event);
        return event;
    }
}