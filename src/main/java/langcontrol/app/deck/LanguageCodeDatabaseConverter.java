package langcontrol.app.deck;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import langcontrol.app.exception.InvalidDatabaseValueException;

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
