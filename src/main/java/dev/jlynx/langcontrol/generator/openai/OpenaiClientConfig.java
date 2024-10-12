package dev.jlynx.langcontrol.generator.openai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenaiClientConfig {

    private static final String baseUrl = "https://api.openai.com/v1";

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Bean(name = "openaiWebClient")
    public WebClient openaiWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }
}
