package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.flashcard.WordFlashcard;
import dev.jlynx.langcontrol.flashcard.WordFlashcardService;
import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpacedRepetitionServiceImpl implements SpacedRepetitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpacedRepetitionServiceImpl.class);

    private final WordFlashcardService wordFlashcardService;
    private final SpacedRepetitionAlgorithm spacedRepetitionAlgorithm;

    @Autowired
    public SpacedRepetitionServiceImpl(WordFlashcardService wordFlashcardService,
                                       SpacedRepetitionAlgorithm spacedRepetitionAlgorithm) {
        this.wordFlashcardService = wordFlashcardService;
        this.spacedRepetitionAlgorithm = spacedRepetitionAlgorithm;
    }

    @Transactional
    @Override
    public FlashcardRatingResponse applyRating(Long flashcardId, RatingType ratingType) {
        if (flashcardId == null || ratingType == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }
        WordFlashcard flashcard = wordFlashcardService.getCardById(flashcardId);
        boolean inLearnModeBefore = flashcard.isInLearnMode();

        spacedRepetitionAlgorithm.apply(flashcard, ratingType);
        LOGGER.info("Rating applied. Next view datetime in UTC is: {}", flashcard.getNextView());

        boolean inLearnModeAfter = flashcard.isInLearnMode();
        boolean switchedToReviewMode = inLearnModeBefore && !inLearnModeAfter;
        boolean switchedToLearnMode = !inLearnModeBefore && inLearnModeAfter;

        return new FlashcardRatingResponse(flashcard.getId(),
                switchedToReviewMode, switchedToLearnMode);
    }
}
