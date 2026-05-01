package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.ChatRequest;
import com.unipart.unipart_backend.dto.response.AIResponse;

public interface ChatService {
    AIResponse chat(ChatRequest chatRequest);
}
