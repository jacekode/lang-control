package langcontrol.app.generator;

import langcontrol.app.deck.LanguageCode;
import langcontrol.app.flashcard.PartOfSpeech;

import java.util.List;

public interface SentenceGenerator {

    List<String> generate(String keyword, LanguageCode keywordLanguage,
                          PartOfSpeech keywordPartOfSpeech, int numberOfSentences);
}
