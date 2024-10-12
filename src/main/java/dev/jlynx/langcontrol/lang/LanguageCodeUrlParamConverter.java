package dev.jlynx.langcontrol.lang;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts the ISO 639-1 language code value from a URL query parameter to a corresponding {@code LanguageCode} enum.
 */
@Component
public class LanguageCodeUrlParamConverter implements Converter<String, LanguageCode> {

    /**
     * @param source the ISO 639-1 language code from a URL query parameter
     * @return enum with the corresponding language code
     * @throws IllegalArgumentException if the URL query parameter value is invalid
     */
    @Override
    public LanguageCode convert(@NotNull String source) {
        return LanguageCode.findByCode(source.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Value '%s' is not a valid ISO 639-1 language code".formatted(source)
                ));
    }
}
