package dev.jlynx.langcontrol.url;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts the value from a URL query parameter to a corresponding {@code SortOrder} enum.
 */
@Component
public class SortOrderUrlParamConverter implements Converter<String, SortOrder> {

    /**
     * @param source the string value from a URL query parameter
     * @return enum with the corresponding string value
     * @throws IllegalArgumentException if the URL query parameter value is invalid
     */
    @Override
    public SortOrder convert(String source) {
        for (SortOrder order : SortOrder.values()) {
            if (order.getUrlValue().equals(source.toLowerCase())) {
                return order;
            }
        }
        throw new IllegalArgumentException(
                "String '%s' is not a valid SortOrder value".formatted(source)
        );
    }
}
