package com.unipart.unipart_backend.configuration;

import com.unipart.unipart_backend.service.AuthenticationService;
import com.unipart.unipart_backend.dto.request.IntrospectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final CustomJwtDecoder customJwtDecoder;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");
            if (token != null && !token.isBlank()) {
                try {
                    Jwt jwt = customJwtDecoder.decode(token);
                    String userId = jwt.getClaimAsString("userId");
                    String scope = jwt.getClaimAsString("scope");
                    attributes.put("userId", userId);
                    attributes.put("userRole", scope);
                    log.debug("WebSocket auth OK: userId={}, role={}", userId, scope);
                } catch (Exception e) {
                    log.warn("WebSocket invalid token: {}", e.getMessage());
                    // Connect vẫn được phép nhưng userId = null → chỉ đọc, không write
                }
            }
        }
        return true; // luôn cho connect (public read)
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
