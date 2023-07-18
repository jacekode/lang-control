package langcontrol.app.flashcard;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PartOfSpeechEnumConverter implements Converter<String, PartOfSpeech> {

    @Override
    public PartOfSpeech convert(@NotNull String source) {
        return PartOfSpeech.findByStringValue(source.toLowerCase()).orElse(null);
    }
}
