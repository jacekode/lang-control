package langcontrol.app.generator;

import langcontrol.app.deck.LanguageCode;
import langcontrol.app.flashcard.PartOfSpeech;

import java.util.Map;

public interface SentenceWithTranslationGenerator {

    Map<String, String> generate(String keyword, LanguageCode keywordLanguage, PartOfSpeech keywordPos,
                                 int numberOfSentences);
}
