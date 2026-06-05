package com.unipart.unipart_backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.unipart.unipart_backend.configuration.CustomJwtDecoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {"/users/**", "/auth/**","/chat","/job/**","/otp/**"};

    @Autowired
    private CustomJwtDecoder customJwtDecoder;
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.authorizeHttpRequests(requests ->
                    requests.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers("/ws/**").permitAll()
                            .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                            .requestMatchers(HttpMethod.POST, "/home/payment/payos-webhook").permitAll()
                            .requestMatchers(HttpMethod.GET, "/auth/**", "/packages", "/packages/**", "/payment/success").permitAll()
                            .requestMatchers(HttpMethod.GET, "/reviews/employer/**", "/reviews/student/**").permitAll()
                            // Community Post — public read
                            .requestMatchers(HttpMethod.GET, "/categories", "/categories/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/posts", "/posts/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/posts/filter").permitAll()
                            .requestMatchers(HttpMethod.GET, "/comments/post/**").permitAll()
                            // WebSocket handshake (SockJS)
                            .requestMatchers("/ws/**").permitAll()
                            .anyRequest().authenticated());
            httpSecurity
                    .cors(cors -> {})
                    .csrf(AbstractHttpConfigurer::disable);
            httpSecurity.oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwtConfigurer ->
                            jwtConfigurer.decoder(customJwtDecoder)
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                    ).authenticationEntryPoint(new JwtAuthenticationEntryPoint())


            );
            return httpSecurity.build();
        }
        @Bean
        JwtAuthenticationConverter jwtAuthenticationConverter(){
            JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
        }

    @Bean
    PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder(10);
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(java.util.List.of("http://localhost:5173", "https://unipart.vercel.app", "https://unihire.online", "http://unihire.online"));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

