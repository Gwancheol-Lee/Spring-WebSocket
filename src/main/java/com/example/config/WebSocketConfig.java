package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@EnableWebSocketMessageBroker // WebSocket 서버 활성화
@Configuration                // 빈 등록 선언
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5500", "http://localhost:8080", "http://1.212.9.203:39083" }, allowedHeaders = "*")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // WebSocket 연결을 구성하기 위한 메서드 구현 및 제공

	// Client가 WebSocket에 연결하는 데 사용할 WebSokcet EndPoint를 등록. EndPoint 구성에 withSockJS()를 사용.
	// SockJS는 WebSocket을 지원하지 않는 브라우저에 Fallback 옵션을 활성화 하는데 사용됨.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
    
    // A Client에서 B Client로 메시지를 라우팅 하는데 사용될 메시지 브로커를 구성 
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) { 
//        registry.setApplicationDestinationPrefixes("/pub"); // "/app" 시작되는 메시지가 message-handling 메소드로 라우팅 되도록 명시
        registry.enableSimpleBroker("/sub");				// "/topic", "/queue" 시작되는 메시지가 메시지 브로커로 라우팅 되도록 명시
    }
}