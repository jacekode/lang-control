package dev.jlynx.langcontrol.generator.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatCompletionRequest(
        String model,
        List<ChatMessage> messages,
        @JsonProperty("max_completion_tokens")
        Integer maxCompletionTokens,
        Integer n,
        Double temperature
) {

    public record ChatMessage(
            ChatMessageRole role,
            String content
    ) {}
}
