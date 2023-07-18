package langcontrol.app.generator;

import langcontrol.app.deck.LanguageCode;

public interface Translator {

    default String translate(String textToTranslate, LanguageCode translateTo) {
        return "";
    }

    default String translate(String textToTranslate, LanguageCode translateTo, LanguageCode translateFrom) {
        return "";
    }
}
