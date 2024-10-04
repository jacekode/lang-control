package dev.jlynx.langcontrol.generator;

import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.flashcard.PartOfSpeech;

import java.util.List;

/**
 * Defines operations related to generating sentences.
 */
public interface SentenceGenerator {

    /**
     * Generates a list of sentences which all contain the specified keyword.
     *
     * @param keyword a word which all generated sentences should contain
     * @param keywordLang the language of the keyword and thus, the desired language for sentences
     * @param keywordPos the grammatical part of speech of the keyword
     * @param numberOfSentences desired number of sentences to generate
     * @return a list of sentences containing the keyword
     */
    List<String> generate(String keyword, LanguageCode keywordLang,
                          PartOfSpeech keywordPos, int numberOfSentences);
}
