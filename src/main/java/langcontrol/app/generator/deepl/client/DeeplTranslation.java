package langcontrol.app.generator.deepl.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeeplTranslation {

    @JsonProperty("detected_source_language")
    String detectedSourceLanguageCode;

    @JsonProperty("text")
    private String translatedText;
}
