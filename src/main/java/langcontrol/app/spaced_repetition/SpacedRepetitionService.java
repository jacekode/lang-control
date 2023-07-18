package langcontrol.app.spaced_repetition;

import langcontrol.app.flashcard.Flashcard;

public interface SpacedRepetitionService {

    FlashcardRatedOverviewDTO applyRating(Long flashcardId, RatingType ratingType);

}
