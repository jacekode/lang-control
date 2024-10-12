package dev.jlynx.langcontrol.flashcard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jlynx.langcontrol.deck.Deck;
import dev.jlynx.langcontrol.flashcard.PartOfSpeech;
import dev.jlynx.langcontrol.flashcard.WordFlashcard;
import dev.jlynx.langcontrol.lang.LanguageCode;

import java.time.LocalDateTime;

/**
 * Spring Data projection for {@link WordFlashcard}
 */
public interface WordFlashcardView {

    Long getId();

    boolean isInLearnMode();

    LanguageCode getSourceLang();

    LanguageCode getTargetLang();

    String getTranslatedWord();

    String getTargetWord();

    PartOfSpeech getPartOfSpeech();

    boolean isDynamicExamples();

    String getTargetExample();

    String getTranslatedExample();

    @JsonProperty("currentIntervalMinutes")
    Long getCurrentInterval();

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
    LocalDateTime getCreatedAt();

    LocalDateTime getNextView();

    DeckInfo getDeck();

    /**
     * Spring Data projection for {@link Deck}
     */
    interface DeckInfo {

        Long getId();

        String getName();
    }
}
