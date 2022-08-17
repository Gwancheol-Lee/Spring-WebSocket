package com.example.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.model.ChatMessage;
import com.example.model.MessageType;


@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) { 
    	// ChatController의 addUesr() 메서드에서 사용자 참여 이벤트를 BroadCast 하기 때문에 로깅만 남김
    	StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    	// WebSocket 사용자 이름 추출
    	
    	System.out.println("headerAccessor " + headerAccessor);
        System.out.println("getSessionId " + headerAccessor.getSessionId());
        System.out.println("getDestination " + headerAccessor.getSubscriptionId());
        logger.info("Received a new web socket connection"); 
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // WebSocket 사용자 이름 추출
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String sessionId = headerAccessor.getSessionId();
        
        if(sessionId != null) {
            logger.info("User Disconnected : " + username);
            
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(MessageType.LEAVE);
            chatMessage.setSender(username);
            chatMessage.setSessionId(sessionId);
            // 연결된 모든 클라이언트에게 사용자 퇴장 이벤트를 Broadcast
            messagingTemplate.convertAndSend("/sub/chat", chatMessage);
        }
    }
}
