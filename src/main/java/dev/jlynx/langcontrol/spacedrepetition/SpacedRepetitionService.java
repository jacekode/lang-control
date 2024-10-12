package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingResponse;

public interface SpacedRepetitionService {

    FlashcardRatingResponse applyRating(Long flashcardId, RatingType ratingType);

}
