package langcontrol.app.flashcard;

import langcontrol.app.deck.Deck;
import langcontrol.app.flashcard.rest.FlashcardForecastsDTO;

import java.util.Deque;
import java.util.List;

public interface FlashcardService {

    void deleteFlashcard(long flashcardId);

    void createNewFlashcard(long deckId, FlashcardCreationDTO dto);

    void createNewFlashcardZenMode(long deckId, FlashcardZenModeCreationDTO dto);

    Deque<Flashcard> fetchReadyForReviewShuffledWithLimit(Long deckId, String zoneId, int limit);

    List<Flashcard> getAllFlashcardsByDeck(Deck deck);

    FlashcardForecastsDTO produceReviewTimeForecastsAsText(long flashcardId);

    Flashcard getCardById(long id);
}
