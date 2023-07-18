package langcontrol.app.generator;

import langcontrol.app.flashcard.PartOfSpeech;
import langcontrol.app.deck.LanguageCode;

import java.util.List;

public interface Dictionary {

    List<String> getTranslationsList(String wordOrPhraseToTranslate, LanguageCode translateFrom,
                                     LanguageCode translateTo, PartOfSpeech partOfSpeech);
}
