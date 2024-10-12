package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.deck.Deck;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Provides custom repository methods for managing WordFlashcard objects.
 */
public interface WordFlashcardRepositoryCustom {

    /**
     * An abstract method that fetches a specified number of WordFlashcard objects from a given Deck whose next view
     * datetime is earlier or equal to viewsBeforeDatetime.
     *
     * @param deck                 The deck to fetch the flashcards from.
     * @param viewsBeforeDatetime Only flashcards having the next UTC view earlier or equal to this UTC datetime will be fetched.
     * @return A List of WordFlashcard objects.
     */
    List<WordFlashcard> findAllReadyForViewByDeck(Deck deck, LocalDateTime viewsBeforeDatetime);

}
