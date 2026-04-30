package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.ChatRequest;
import com.unipart.unipart_backend.dto.response.AIResponse;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    @PostMapping
    public ApiResponse<AIResponse> sendMessage(@RequestBody ChatRequest chatRequest) {
        return ApiResponse.<AIResponse>builder()
                .result(chatService.chat(chatRequest))
                .build();
    }
}
