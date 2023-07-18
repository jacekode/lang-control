package langcontrol.app.spaced_repetition;

import langcontrol.app.flashcard.LearnModeStep;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class BasicSpacedRepetitionAlgorithm implements SpacedRepetition {

    private static final double MIN_INTERVAL_DAYS = 2.0;

    @Override
    public void apply(SpacedRepetitionItem item, RatingType rating) {
        if (item == null || rating == null) {
            throw new IllegalArgumentException("The arguments cannot be null when the algorithm is applied.");
        }
        if (rating.name().startsWith("LEARN")) {
            applyLearnRating(item, rating);
        } else if (rating.name().startsWith("REVIEW")) {
            applyReviewRating(item, rating);
        }
    }

    private void applyLearnRating(SpacedRepetitionItem item, RatingType rating) {
        if (!item.isInLearnMode() || item.getLearnModeStep() == null) {
            throw new IllegalStateException("Rating couldn't be applied to the flashcard.");
        }
        switch (rating) {
            case LEARN_PREVIOUS -> stepDownInLearnMode(item);
            case LEARN_NORMAL -> maintainLearnModeStep(item);
            case LEARN_NEXT -> stepUpInLearnMode(item);
            case LEARN_TO_REVIEW_MODE -> switchToReviewMode(item);
        }
    }

    private void applyReviewRating(SpacedRepetitionItem item, RatingType rating) {
        if (item.inLearnMode || item.currentIntervalDays == null) {
            throw new IllegalStateException("Rating couldn't be applied to the flashcard.");
        }
        if (item.currentIntervalDays <= MIN_INTERVAL_DAYS && rating == RatingType.REVIEW_CANNOT_SOLVE) {
            switchToLearnMode(item);
        } else {
            item.lastReviewInUTC = LocalDateTime.now(Clock.systemUTC());
            switch (rating) {
                case REVIEW_CANNOT_SOLVE -> item.currentIntervalDays = MIN_INTERVAL_DAYS;
                case REVIEW_DIFFICULT -> {
                    item.setCurrentIntervalDays(item.getCurrentIntervalDays() * item.getReduceFactor());
                    item.decreaseIFactorByOneUnit();
                    item.decreaseRFactorByOneUnit();
                }
                case REVIEW_NORMAL -> item.setCurrentIntervalDays(item.getCurrentIntervalDays() * 1.1);
                case REVIEW_EASY -> {
                    item.setCurrentIntervalDays(item.getCurrentIntervalDays() * item.getIncreaseFactor());
                    item.increaseIFactorByOneUnit();
                    item.increaseRFactorByOneUnit();
                }
            }
            item.calculateNextReviewDate();
        }
    }

    private void stepUpInLearnMode(SpacedRepetitionItem item) {
        if (item.getLearnModeStep() == LearnModeStep.THREE) {
            switchToReviewMode(item);
            return;
        }
        item.setLearnModeStep(LearnModeStep.getByNumeral(item.getLearnModeStep().getNumeral() + 1));
        LocalDateTime newNextLearnViewInUTC = LocalDateTime.now(Clock.systemUTC())
                .plus(item.getLearnModeStep().getAmountToAdd(),
                        item.getLearnModeStep().getTemporalUnit());
        item.setNextLearnViewInUTC(newNextLearnViewInUTC);
    }

    private void stepDownInLearnMode(SpacedRepetitionItem item) {
        if (item.getLearnModeStep() == LearnModeStep.ONE) {
            item.setNextLearnViewInUTC(LocalDateTime.now(Clock.systemUTC()));
            return;
        }
        item.setLearnModeStep(LearnModeStep.getByNumeral(item.getLearnModeStep().getNumeral() - 1));
        LocalDateTime newNextLearnViewInUTC = LocalDateTime.now(Clock.systemUTC())
                .plus(item.getLearnModeStep().getAmountToAdd(),
                        item.getLearnModeStep().getTemporalUnit());
        item.setNextLearnViewInUTC(newNextLearnViewInUTC);
    }

    private void maintainLearnModeStep(SpacedRepetitionItem item) {
        item.setNextLearnViewInUTC(LocalDateTime.now(Clock.systemUTC())
                .plus(item.getLearnModeStep().getAmountToAdd(),
                        item.getLearnModeStep().getTemporalUnit()));
    }

    private void switchToLearnMode(SpacedRepetitionItem item) {
        if (item.isInLearnMode() || item.getLearnModeStep() != null || item.getNextLearnViewInUTC() != null) {
            return;
        }
        item.setInLearnMode(true);
        item.setLearnModeStep(LearnModeStep.ONE);
        item.setNextLearnViewInUTC(LocalDateTime.now(Clock.systemUTC())
                .plus(item.getLearnModeStep().getAmountToAdd(),
                item.getLearnModeStep().getTemporalUnit()));
    }

    private void switchToReviewMode(SpacedRepetitionItem item) {
        if (!item.isInLearnMode()) {
            return;
        }
        item.setInLearnMode(false);
        item.setLearnModeStep(null);
        item.setNextLearnViewInUTC(null);
        if (item.getCurrentIntervalDays() == null || item.getNextReviewInUTC() == null) {
            item.setCurrentIntervalDays(MIN_INTERVAL_DAYS);
            item.setNextReviewInUTC(LocalDateTime.now(Clock.systemUTC()).plusDays(2)/*.with(LocalTime.MIDNIGHT)*/);
            item.setNextReviewWithoutTimeInUTC(item.getNextReviewInUTC().toLocalDate());
        } else {
            item.calculateNextReviewDate();
        }
    }
}
