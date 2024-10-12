package dev.jlynx.langcontrol.flashcard;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts the string value from a URL query parameter to a corresponding {@code PartOfSpeech} enum.
 */
@Component
public class PartOfSpeechUrlParamConverter implements Converter<String, PartOfSpeech> {

    /**
     * @param source the string value from a URL query parameter
     * @return enum with the corresponding stringValue
     * @throws IllegalArgumentException if the URL query parameter value is invalid
     */
    @Override
    public PartOfSpeech convert(@NotNull String source) {
        return PartOfSpeech.findByStringValue(source.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Value '%s' is not a valid part of speech string value".formatted(source)
                ));
    }
}
