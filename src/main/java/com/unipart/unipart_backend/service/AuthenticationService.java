package com.unipart.unipart_backend.service;

import com.nimbusds.jose.JOSEException;
import com.unipart.unipart_backend.dto.request.AuthenticationRequest;
import com.unipart.unipart_backend.dto.request.IntrospectRequest;
import com.unipart.unipart_backend.dto.request.LogoutRequest;
import com.unipart.unipart_backend.dto.request.RefreshRequest;
import com.unipart.unipart_backend.dto.response.AuthenticationResponse;
import com.unipart.unipart_backend.dto.response.IntrospectResponse;
import org.springframework.stereotype.Service;

import java.text.ParseException;


public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    void logout(LogoutRequest request) throws ParseException, JOSEException;
    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}
