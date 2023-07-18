package langcontrol.app.flashcard;

import langcontrol.app.deck.Deck;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FlashcardRepositoryCustom {

    List<Flashcard> findReadyForReviewFlashcardsByDeck(Deck deck,
                                                       LocalDateTime nextLearnViewInUTCBefore,
                                                       LocalDate nextReviewDateLocalBefore,
                                                       int limit);

}
