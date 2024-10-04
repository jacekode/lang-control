package dev.jlynx.langcontrol.lang;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import dev.jlynx.langcontrol.exception.InvalidDatabaseValueException;

/**
 * Provides the necessary logic for conversions between the {@link LanguageCode} enum and a database
 * column of type {@code varchar}.
 */
@Converter(autoApply = true)
public class LanguageCodeDatabaseConverter implements AttributeConverter<LanguageCode, String> {

    @Override
    public String convertToDatabaseColumn(LanguageCode languageCode) {
        return languageCode.getCode();
    }

    @Override
    public LanguageCode convertToEntityAttribute(String code) {
        return LanguageCode.findByCode(code)
                .orElseThrow(() ->
                        new InvalidDatabaseValueException("Could not find a language code \"%s\"".formatted(code))
                );
    }
}
