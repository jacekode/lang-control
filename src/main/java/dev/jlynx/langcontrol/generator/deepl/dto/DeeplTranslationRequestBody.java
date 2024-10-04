package dev.jlynx.langcontrol.generator.deepl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeeplTranslationRequestBody {

    @JsonProperty("text")
    private List<String> textToTranslate;

    @JsonProperty("target_lang")
    private String targetLanguageCode;
}
