package com.demo.service;

/**
 * AI聊天服务接口
 */
public interface AiChatService {
    /**
     * 处理AI聊天请求
     * @param userId 老人ID
     * @param message 老人消息
     * @return AI回复
     */
    String chat(Long userId, String message);
}
