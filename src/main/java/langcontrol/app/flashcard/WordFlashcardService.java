package langcontrol.app.flashcard;

import langcontrol.app.deck.Deck;

import java.util.Deque;
import java.util.List;

public interface WordFlashcardService {

    void createNewFlashcard(long deckId, FlashcardCreationDTO dto);

    void createNewFlashcardZenMode(long deckId, FlashcardZenModeCreationDTO dto);

    WordFlashcard getCardById(long id);

    List<WordFlashcard> getAllFlashcardsByDeck(Deck deck);

    void deleteFlashcard(long flashcardId);

    Deque<WordFlashcard> fetchShuffleReadyForView(Long deckId, int limit);

    IntervalForecastDTO produceReviewTimeForecastsAsText(long flashcardId);
}
