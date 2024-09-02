package langcontrol.app.spacedrepetition;

public interface SpacedRepetitionService {

    FlashcardRatedOverviewDTO applyRating(Long flashcardId, RatingType ratingType);

}
