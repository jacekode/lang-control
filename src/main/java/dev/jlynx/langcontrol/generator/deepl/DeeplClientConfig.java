package dev.jlynx.langcontrol.generator.deepl;

import com.deepl.api.Translator;
import com.deepl.api.TranslatorOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class DeeplClientConfig {

    @Value("${deepl.api.key}")
    private String deeplApiKey;

    @Value("${app.version}")
    private String appVersion;

//    @Bean(name = "deeplWebClient")
//    public WebClient deeplWebClient() {
//        return WebClient.builder()
//                .baseUrl("https://api-free.deepl.com/v2")
//                .defaultHeader("Authorization", "DeepL-Auth-Key " + deeplApiKey)
//                .build();
//    }

    @Bean
    public Translator deeplClientTranslator() {
        TranslatorOptions options = new TranslatorOptions()
                .setTimeout(Duration.ofMillis(1000))
                .setMaxRetries(3)
                .setAppInfo("LangControl", appVersion);
        return new Translator(deeplApiKey, options);
    }
}
