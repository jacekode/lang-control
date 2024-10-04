package dev.jlynx.langcontrol.generator.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatCompletionResponse(
        String id,
        List<Choice> choices,
        int created,
        @JsonProperty("system_fingerprint")
        String systemFingerprint,
        String object,
        UsageStatistics usage
) {

    public record Choice(
            @JsonProperty("finish_reason")
            String finishReason,
            int index,
            ChatResponseMessage message
    ) {}

    public record ChatResponseMessage(
            ChatMessageRole role,
            String content,
            String refusal
    ) {}

    public record UsageStatistics(
            @JsonProperty("completion_tokens")
            int completionTokens,
            @JsonProperty("prompt_tokens")
            int promptTokens,
            @JsonProperty("total_tokens")
            int totalTokens
    ) {}
}
