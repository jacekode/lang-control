package langcontrol.app.flashcard;

import langcontrol.app.deck.Deck;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Provides custom repository methods for managing WordFlashcard objects.
 */
public interface WordFlashcardRepositoryCustom {

    /**
     * An abstract method that fetches a specified number of WordFlashcard objects from a given Deck whose next view
     * datetime is earlier or equal to viewsBeforeTimestamp.
     *
     * @param deck The deck to fetch the flashcards from.
     * @param viewsBeforeTimestamp Only flashcards having the next view earlier or equal to this value will be fetched.
     * @param limit The maximum number of flashcards to fetch.
     * @return A List of WordFlashcard objects.
     */
    List<WordFlashcard> findReadyForReviewFlashcardsByDeck(Deck deck,
                                                           LocalDateTime viewsBeforeTimestamp,
                                                           int limit);

}
