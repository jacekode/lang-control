package langcontrol.app.generator.deepl.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DeeplClientConfiguration {

    @Value("${deepl.api.key}")
    private String deeplApiKey;

    @Bean(name = "deeplWebClient")
    public WebClient deeplWebClient() {
        return WebClient.builder()
                .baseUrl("https://api-free.deepl.com/v2")
                .defaultHeader("Authorization", "DeepL-Auth-Key " + deeplApiKey)
                .build();
    }
}
