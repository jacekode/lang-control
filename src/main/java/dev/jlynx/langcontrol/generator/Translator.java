package dev.jlynx.langcontrol.generator;

import dev.jlynx.langcontrol.lang.LanguageCode;

public interface Translator {

    default String translate(String textToTranslate, LanguageCode translateTo) {
        throw new UnsupportedOperationException("No interface method implementation was found.");
    }

    default String translate(String textToTranslate, LanguageCode translateTo, LanguageCode translateFrom) {
        throw new UnsupportedOperationException("No interface method implementation was found.");
    }
}
