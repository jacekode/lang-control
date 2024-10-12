package dev.jlynx.langcontrol.flashcard;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts the value from a URL query parameter to a corresponding {@code FlashcardSortBy} enum.
 */
@Component
public class FlashcardSortByUrlParamConverter implements Converter<String, FlashcardSortBy> {

    /**
     * @param source the string value from a URL query parameter
     * @return enum with the corresponding string value
     * @throws IllegalArgumentException if the URL query parameter value is invalid
     */
    @Override
    public FlashcardSortBy convert(String source) {
        for (FlashcardSortBy sortBy : FlashcardSortBy.values()) {
            if (sortBy.getUrlValue().equals(source.toLowerCase())) {
                return sortBy;
            }
        }
        throw new IllegalArgumentException(
                "String '%s' is not a valid FlashcardSortBy value".formatted(source)
        );
    }
}
