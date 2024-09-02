package langcontrol.app.spacedrepetition;

import langcontrol.app.flashcard.WordFlashcard;
import langcontrol.app.flashcard.WordFlashcardService;
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
    public FlashcardRatedOverviewDTO applyRating(Long flashcardId, RatingType ratingType) {
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

        return new FlashcardRatedOverviewDTO(flashcard.getId(),
                switchedToReviewMode, switchedToLearnMode);
    }
}
