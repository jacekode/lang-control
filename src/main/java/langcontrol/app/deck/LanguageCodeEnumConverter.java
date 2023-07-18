package langcontrol.app.deck;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LanguageCodeEnumConverter implements Converter<String, LanguageCode> {

    @Override
    public LanguageCode convert(@NotNull String source) {
        return LanguageCode.findByCode(source.toLowerCase()).orElse(null);
    }
}
