package com.unipart.unipart_backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Kích hoạt SimpleBroker để gửi tin nhắn đến các client đăng ký các topic cụ thể.
        // Các tin nhắn gửi đến '/topic' sẽ được broadcast đến tất cả các client đăng ký topic đó.
        // Các tin nhắn gửi đến '/user' sẽ được gửi đến một người dùng cụ thể.
        config.enableSimpleBroker("/topic", "/user");

        // Đặt tiền tố cho các endpoint mà client gửi tin nhắn đến server.
        // Ví dụ: client gửi tin nhắn đến '/app/hello' sẽ được xử lý bởi controller.
        config.setApplicationDestinationPrefixes("/app");

        // Đặt tiền tố cho các tin nhắn dành riêng cho người dùng.
        // Khi gửi tin nhắn đến '/user/queue/notifications', Spring sẽ tự động thêm tiền tố này.
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint '/ws' cho WebSocket, cho phép sử dụng SockJS làm fallback
        // khi WebSocket không khả dụng. SockJS rất hữu ích cho các trình duyệt cũ
        // hoặc trong môi trường mạng có proxy.
        registry.addEndpoint("/ws").withSockJS();
    }
}