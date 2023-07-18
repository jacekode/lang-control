package langcontrol.app.spaced_repetition;

import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.flashcard.Flashcard;
import langcontrol.app.flashcard.FlashcardRepository;
import langcontrol.app.flashcard.FlashcardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpacedRepetitionServiceImpl implements SpacedRepetitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpacedRepetitionServiceImpl.class);

    private final FlashcardService flashcardService;
    private final SpacedRepetition spacedRepetitionAlgorithm;

    @Autowired
    public SpacedRepetitionServiceImpl(FlashcardService flashcardService,
                                       SpacedRepetition spacedRepetitionAlgorithm) {
        this.flashcardService = flashcardService;
        this.spacedRepetitionAlgorithm = spacedRepetitionAlgorithm;
    }

    @Transactional
    @Override
    public FlashcardRatedOverviewDTO applyRating(Long flashcardId, RatingType ratingType) {
        if (flashcardId == null || ratingType == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }
        Flashcard flashcard = flashcardService.getCardById(flashcardId);
        boolean inLearnModeBefore = flashcard.isInLearnMode();

        spacedRepetitionAlgorithm.apply(flashcard, ratingType);
        LOGGER.info("Learn rating applied. Next learn view datetime in UTC is: {}. Next review datetime in UTC is: {}",
                flashcard.getNextLearnViewInUTC(), flashcard.getNextReviewInUTC());

        boolean inLearnModeAfter = flashcard.isInLearnMode();
        boolean switchedToReviewMode = inLearnModeBefore && !inLearnModeAfter;
        boolean switchedToLearnMode = !inLearnModeBefore && inLearnModeAfter;

        return new FlashcardRatedOverviewDTO(flashcard.getId(),
                switchedToReviewMode, switchedToLearnMode);
    }
}
