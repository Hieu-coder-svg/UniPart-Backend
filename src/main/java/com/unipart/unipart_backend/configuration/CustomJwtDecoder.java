package com.unipart.unipart_backend.configuration;

import com.nimbusds.jose.JOSEException;
import com.unipart.unipart_backend.dto.request.IntrospectRequest;
import com.unipart.unipart_backend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.signer_key}")
    private String signerKey;
    @Autowired
   private AuthenticationService authentication;
   private NimbusJwtDecoder jwtDecoder = null;
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authentication.introspect(
                    IntrospectRequest.builder().token(token).build());

            if (!response.isValid()) throw new JwtException("Token invalid");
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }
        if(Objects.isNull(jwtDecoder)){
            SecretKeySpec secretKey = new SecretKeySpec(signerKey.getBytes(), "HS512");
            jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return jwtDecoder.decode(token);
    }
}
