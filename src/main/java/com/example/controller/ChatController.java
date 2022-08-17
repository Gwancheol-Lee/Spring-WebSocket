package com.example.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.model.ChatMessage;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8080" }, allowedHeaders = "*")
public class ChatController {
	
	private final SimpMessagingTemplate template;
	 
	@MessageMapping("/pub/chat/{userId}")
    public void sendMessage(@DestinationVariable String userId, @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		System.out.println("userId: " + userId);
		headerAccessor.getSessionAttributes().put("userid", userId);
		System.out.println("sendMessage: " + chatMessage);
		template.convertAndSend("/sub/chat/" + userId, chatMessage);
    }
	
	@MessageMapping("/pub/chat")
    @SendTo("/sub/chat")
    public ChatMessage sendMessageAll(@Payload ChatMessage chatMessage) {
		System.out.println("sendMessage: " + chatMessage);
        return chatMessage;
    }

    @MessageMapping("/pub/chat/user")
    @SendTo("/sub/chat")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        System.out.println("addUser: " + chatMessage);
        return chatMessage;
    }
    
    @MessageMapping("/pub/chat/leave")
    @SendTo("/sub/chat")
    public ChatMessage  leave(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    	chatMessage.setSessionId(headerAccessor.getSessionId());
		System.out.println("leave: " + chatMessage);
        return chatMessage;
    }
}