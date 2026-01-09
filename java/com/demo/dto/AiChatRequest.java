package com.demo.dto;

import lombok.Data;

/**
 * AI聊天请求DTO
 */
@Data
public class AiChatRequest {
    private Long userId;     // 老人ID
    private String message;  // 老人说的话
}
