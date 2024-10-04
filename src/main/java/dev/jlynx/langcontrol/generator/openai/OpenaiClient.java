package dev.jlynx.langcontrol.generator.openai;

import dev.jlynx.langcontrol.exception.OpenaiClientResponseException;
import dev.jlynx.langcontrol.generator.openai.dto.ChatCompletionRequest;
import dev.jlynx.langcontrol.generator.openai.dto.ChatCompletionResponse;
import dev.jlynx.langcontrol.generator.openai.dto.OpenaiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OpenaiClient {

    private static final Logger LOG = LoggerFactory.getLogger(OpenaiClient.class);

    private final WebClient client;

    @Autowired
    public OpenaiClient(@Qualifier("openaiWebClient") WebClient client) {
        this.client = client;
    }

    public ResponseEntity<ChatCompletionResponse> getChatCompletion(ChatCompletionRequest body) {
        ResponseEntity<ChatCompletionResponse> response = client.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> {
                    OpenaiErrorResponse errorBody = resp.bodyToMono(OpenaiErrorResponse.class).block();
                    LOG.debug("OpenAI API responded with status code={}, request URI={}", resp.statusCode().value(), resp.request().getURI());
                    assert errorBody != null;
                    throw new OpenaiClientResponseException(errorBody.error().message());
                })
                .toEntity(ChatCompletionResponse.class)
                .block();
        return response;
    }
}
