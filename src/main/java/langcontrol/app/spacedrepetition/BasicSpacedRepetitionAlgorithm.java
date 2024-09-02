package langcontrol.app.spacedrepetition;

import langcontrol.app.flashcard.Flashcard;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class BasicSpacedRepetitionAlgorithm implements SpacedRepetitionAlgorithm {

    protected static final float FACTOR_UPDATE_UNIT = 0.04F;
    protected static final float I_FACTOR_UPPER_LIMIT = 1.4F;
    protected static final float I_FACTOR_BOTTOM_LIMIT = 1.1F;
    protected static final float R_FACTOR_UPPER_LIMIT = 0.5F;
    protected static final float R_FACTOR_BOTTOM_LIMIT = 0.2F;
    public static final long INITIAL_REVIEW_INTERVAL = Flashcard.INITIAL_REVIEW_INTERVAL;

    @Override
    public void apply(Flashcard card, RatingType rating) {
        if (card == null || rating == null) {
            throw new IllegalArgumentException("The arguments cannot be null when the algorithm is applied.");
        }
        if (rating.name().startsWith("LEARN")) {
            applyLearnRating(card, rating);
        } else if (rating.name().startsWith("REVIEW")) {
            applyReviewRating(card, rating);
        }
    }

    @Override
    public Long calculateNextInterval(Flashcard card, RatingType rating) {
        if (card.isInLearnMode()) {
            switch (card.getLearnModeStep()) {
                case ONE -> {
                    if (rating == RatingType.LEARN_KNOW) {
                        return Duration
                                .of(LearnModeStep.TWO.getAmountToAdd(), LearnModeStep.TWO.getTemporalUnit())
                                .toMinutes();
                    }
                    if (rating == RatingType.LEARN_DONT_KNOW) {
                        return Duration
                                .of(LearnModeStep.ONE.getAmountToAdd(), LearnModeStep.ONE.getTemporalUnit())
                                .toMinutes();
                    }
                }
                case TWO -> {
                    if (rating == RatingType.LEARN_KNOW) {
                        return Flashcard.INITIAL_REVIEW_INTERVAL;
                    }
                    if (rating == RatingType.LEARN_DONT_KNOW) {
                        return Duration
                                .of(LearnModeStep.ONE.getAmountToAdd(), LearnModeStep.ONE.getTemporalUnit())
                                .toMinutes();
                    }
                }
            }
        } else {
            if (rating == RatingType.REVIEW_REMEMBER) {
                return (long) (card.getCurrentInterval() * card.getIFactor());
            }
            if (rating == RatingType.REVIEW_PARTIALLY) {
                return (long) (card.getCurrentInterval() * card.getRFactor());
            }
            if (rating == RatingType.REVIEW_FORGOT) {
                return Flashcard.INITIAL_REVIEW_INTERVAL;
            }
        }
        return null;
    }

    private void applyLearnRating(Flashcard item, RatingType rating) {
        if (!item.isInLearnMode() || item.getLearnModeStep() == null) {
            throw new IllegalStateException("Learn rating couldn't be applied to the flashcard in review mode.");
        }
        if (!rating.name().startsWith("LEARN")) {
            throw new IllegalArgumentException("Expected a learn mode RatingType.");
        }
        switch (rating) {
            case LEARN_KNOW -> stepUpInLearnMode(item);
            case LEARN_DONT_KNOW -> stepDownInLearnMode(item);
        }
    }

    private void applyReviewRating(Flashcard card, RatingType rating) {
        if (card.isInLearnMode()) {
            throw new IllegalStateException("Rating couldn't be applied to the flashcard.");
        }
        if (!rating.name().startsWith("REVIEW")) {
            throw new IllegalArgumentException("Expected a review mode RatingType.");
        }
        switch (rating) {
            case REVIEW_FORGOT -> card.updateNextView(INITIAL_REVIEW_INTERVAL);
            case REVIEW_PARTIALLY -> {
                card.updateNextView((long) (card.getCurrentInterval() * card.getRFactor()));
                decreaseIFactorByOneUnit(card);
                decreaseRFactorByOneUnit(card);
            }
            case REVIEW_REMEMBER -> {
                card.updateNextView((long) (card.getCurrentInterval() * card.getIFactor()));
                increaseIFactorByOneUnit(card);
                increaseRFactorByOneUnit(card);
            }
        }
    }

    private void stepUpInLearnMode(Flashcard item) {
        if (item.getLearnModeStep() == LearnModeStep.TWO) {
            switchToReviewMode(item);
        } else {
            item.setLearnModeStep(LearnModeStep.fromNumeral(item.getLearnModeStep().getNumeral() + 1));
            LocalDateTime nextViewUTC = LocalDateTime.now(Clock.systemUTC())
                    .plus(item.getLearnModeStep().getAmountToAdd(),
                            item.getLearnModeStep().getTemporalUnit());
            item.setNextView(nextViewUTC);
        }
    }

    private void stepDownInLearnMode(Flashcard item) {
        if (item.getLearnModeStep() != LearnModeStep.ONE) {
            item.setLearnModeStep(LearnModeStep.fromNumeral(item.getLearnModeStep().getNumeral() - 1));
        } else {
            LocalDateTime nextViewUTC = LocalDateTime.now(Clock.systemUTC())
                    .plus(item.getLearnModeStep().getAmountToAdd(),
                            item.getLearnModeStep().getTemporalUnit());
            item.setNextView(nextViewUTC);
        }
    }

    private boolean switchToReviewMode(Flashcard item) {
        if (!item.isInLearnMode()) {
            return false;
        }
        item.setInLearnMode(false);
        item.setLearnModeStep(null);
        item.updateNextView(INITIAL_REVIEW_INTERVAL);
        return true;
    }

    private void increaseIFactorByOneUnit(Flashcard sri) {
        sri.setIFactor(sri.getIFactor() + FACTOR_UPDATE_UNIT);
        if (sri.getIFactor() > I_FACTOR_UPPER_LIMIT) {
            sri.setIFactor(I_FACTOR_UPPER_LIMIT);
        }
    }

    private void decreaseIFactorByOneUnit(Flashcard sri) {
        sri.setIFactor(sri.getIFactor() - FACTOR_UPDATE_UNIT);
        if (sri.getIFactor() < I_FACTOR_BOTTOM_LIMIT) {
            sri.setIFactor(I_FACTOR_BOTTOM_LIMIT);
        }
    }

    private void increaseRFactorByOneUnit(Flashcard sri) {
        sri.setRFactor(sri.getRFactor() + FACTOR_UPDATE_UNIT);
        if (sri.getRFactor() > R_FACTOR_UPPER_LIMIT) {
            sri.setRFactor(R_FACTOR_UPPER_LIMIT);
        }
    }

    private void decreaseRFactorByOneUnit(Flashcard sri) {
        sri.setRFactor(sri.getRFactor() - FACTOR_UPDATE_UNIT);
        if (sri.getRFactor() < R_FACTOR_BOTTOM_LIMIT) {
            sri.setRFactor(R_FACTOR_BOTTOM_LIMIT);
        }
    }
}
