package dev.jlynx.langcontrol.spacedrepetition;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts the value from a URL query parameter to a corresponding {@code SpacedRepetitionSortBy} enum.
 */
@Component
public class SpacedRepetitionSortByUrlParamConverter implements Converter<String, SpacedRepetitionSortBy> {

    /**
     * @param source the string value from a URL query parameter
     * @return enum with the corresponding string value
     * @throws IllegalArgumentException if the URL query parameter value is invalid
     */
    @Override
    public SpacedRepetitionSortBy convert(String source) {
        for (SpacedRepetitionSortBy sortBy : SpacedRepetitionSortBy.values()) {
            if (sortBy.getUrlValue().equals(source.toLowerCase())) {
                return sortBy;
            }
        }
        throw new IllegalArgumentException(
                "String '%s' is not a valid SpacedRepetitionSortBy value".formatted(source)
        );
    }
}
