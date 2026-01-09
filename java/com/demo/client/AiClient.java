package com.demo.client;

import org.springframework.stereotype.Component;

/**
 * AI客户端，封装豆包云AI接口调用
 */
@Component
public class AiClient {

    // 豆包云API地址（示例）
    private static final String AI_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";

    /**
     * 调用AI聊天接口
     * @param content 聊天内容
     * @return AI回复
     */
    public String chat(String content) {
        // 这里调用豆包云AI接口
        // 由于是示例，返回模拟回复
        // 实际使用时需要替换为真实的HTTP请求
        return "您好！我是您的健康助手小护。" + 
               "我理解您的意思，" + 
               "这是一个模拟回复，实际使用时会调用真实的AI接口。";
    }
}
