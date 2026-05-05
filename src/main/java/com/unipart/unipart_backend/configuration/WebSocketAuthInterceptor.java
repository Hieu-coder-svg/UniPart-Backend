package com.unipart.unipart_backend.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final CustomJwtDecoder customJwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    Jwt jwt = customJwtDecoder.decode(token);
                    String username = jwt.getSubject();

                    List<String> roles = jwt.getClaimAsStringList("roles");
                    List<SimpleGrantedAuthority> authorities = List.of();
                    if (roles != null) {
                        authorities = roles.stream()
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.replace("ROLE_", "")))
                                .collect(Collectors.toList());
                    }

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    accessor.setUser(auth);
                    log.info("WebSocket authenticated: {}", username);
                } catch (Exception e) {
                    log.warn("WebSocket auth failed: {}", e.getMessage());
                }
            }
        }
        return message;
    }
}
