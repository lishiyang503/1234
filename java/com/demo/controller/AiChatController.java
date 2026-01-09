package com.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin // 如果你前端是 vite，必须加
public class AiChatController {

    private static final String OLLAMA_URL =
            "http://localhost:11434/api/generate";

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> body) {

        try {
            // 1️⃣ 取问题
            Object q = body.get("question");
            if (q == null || q.toString().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("code", 400, "msg", "question 不能为空")
                );
            }
            String question = q.toString();

            // 2️⃣ 构建 RestTemplate（⚠ 正确的 Duration）
            RestTemplate restTemplate = new RestTemplateBuilder()
                    .setConnectTimeout(Duration.ofSeconds(10))
                    .setReadTimeout(Duration.ofSeconds(120))
                    .build();

            // 3️⃣ 请求体
            Map<String, Object> req = new HashMap<>();
            req.put("model", "qwen2.5:0.5b");
            req.put("prompt", question);
            req.put("stream", false);

            // 4️⃣ 请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(req, headers);

            // 5️⃣ 调用 Ollama（用 String 接）
            String raw = restTemplate.postForObject(
                    OLLAMA_URL,
                    entity,
                    String.class
            );

            if (raw == null) {
                return ResponseEntity.status(500).body(
                        Map.of("code", 500, "msg", "Ollama 返回为空")
                );
            }

            // 6️⃣ 解析 response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(raw);

            String answer = jsonNode.has("response")
                    ? jsonNode.get("response").asText()
                    : "";

            return ResponseEntity.ok(
                    Map.of(
                            "code", 200,
                            "data", answer
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                    Map.of(
                            "code", 500,
                            "msg", "AI 服务异常",
                            "error", e.getMessage()
                    )
            );
        }
    }
}
