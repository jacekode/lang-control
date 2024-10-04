package dev.jlynx.langcontrol.generator;

import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import dev.jlynx.langcontrol.lang.LanguageCode;

import java.util.List;

/**
 * Defines methods related to translation operations of single words or phrases.
 */
public interface Dictionary {

    /**
     * Generates a list of possible translations of the specified {@code sourceText}.
     * The suggested use case is to fetch a translation list of particular word or phrase as opposed to getting
     * translations of longer sentences or blocks of text.
     *
     * @param sourceText a word or short phrase to be translated
     * @param translateFrom the language of the {@code sourceText}
     * @param translateTo the desired language of the translations
     * @param partOfSpeech indicates the grammatical part of speech of the {@code sourceText}
     * @return a list of strings with {@code sourceText}'s translations
     */
    List<String> getTranslationsList(String sourceText, LanguageCode translateFrom,
                                     LanguageCode translateTo, PartOfSpeech partOfSpeech);
}
