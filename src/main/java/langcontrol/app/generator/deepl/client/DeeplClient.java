package langcontrol.app.generator.deepl.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class DeeplClient {

    private final WebClient webClient;

    @Autowired
    public DeeplClient(@Qualifier("deeplWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public DeeplTranslationResponseBody performTranslateRequest(String textToTranslate, String targetLanguageCode) {
        var requestBody = new DeeplTranslationRequestBody(List.of(textToTranslate), targetLanguageCode);

        DeeplTranslationResponseBody response = webClient
                .post()
                .uri("/translate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(DeeplTranslationResponseBody.class)
                .block();
        return response;
    }

    private String urlEncode(String toBeEncoded) {
        return URLEncoder.encode(toBeEncoded, StandardCharsets.UTF_8);
    }
}
