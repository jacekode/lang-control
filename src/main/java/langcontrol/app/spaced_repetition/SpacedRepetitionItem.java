package langcontrol.app.spaced_repetition;

import jakarta.persistence.*;
import langcontrol.app.flashcard.LearnModeStep;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@DiscriminatorColumn(name = "DATA_TYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@MappedSuperclass
public abstract class SpacedRepetitionItem {

    @Transient
    protected static final double DEFAULT_I_FACTOR = 1.3;

    @Transient
    protected static final double DEFAULT_R_FACTOR = 0.7;

    @Transient
    protected static final double FACTOR_UPDATE_UNIT = 0.04;

    @Transient
    protected static final double I_FACTOR_UPPER_LIMIT = 1.4;

    @Transient
    protected static final double I_FACTOR_BOTTOM_LIMIT = 1.1;

    @Transient
    protected static final double R_FACTOR_UPPER_LIMIT = 0.9;

    @Transient
    protected static final double R_FACTOR_BOTTOM_LIMIT = 0.6;

    @Transient
    protected static final RoundingMode INTERVAL_ROUNDING_MODE = RoundingMode.HALF_DOWN;


    @Column(name = "in_learn_mode", nullable = false)
    protected boolean inLearnMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "learn_mode_step")
    protected LearnModeStep learnModeStep;

    @Column(name = "next_learn_view_in_utc")
    protected LocalDateTime nextLearnViewInUTC;

    @Column(name = "last_review_in_utc")
    protected LocalDateTime lastReviewInUTC;

    @Column(name = "current_interval_days")
    protected Double currentIntervalDays;

    @Column(name = "next_review_in_utc")
    protected LocalDateTime nextReviewInUTC;

    @Column(name = "next_review_without_time_in_utc")
    protected LocalDate nextReviewWithoutTimeInUTC;

    @Column(name = "increase_factor", nullable = false)
    protected Double increaseFactor;

    @Column(name = "reduce_factor", nullable = false)
    protected Double reduceFactor;

    public SpacedRepetitionItem(boolean initialReviewMode) {
        if (!initialReviewMode) {
            this.inLearnMode = true;
            this.learnModeStep = LearnModeStep.ONE;
            this.nextLearnViewInUTC = LocalDateTime.now(Clock.systemUTC());
            this.lastReviewInUTC = null;
            this.currentIntervalDays = null;
            this.nextReviewInUTC = null;
            this.nextReviewWithoutTimeInUTC = null;
        } else {
            this.inLearnMode = false;
            this.learnModeStep = null;
            this.nextLearnViewInUTC = null;
            this.lastReviewInUTC = null;
            this.currentIntervalDays = 2.0;
            this.nextReviewInUTC = LocalDateTime.now(Clock.systemUTC()).plusDays(2);
            this.nextReviewWithoutTimeInUTC = nextReviewInUTC.toLocalDate();
        }
        this.increaseFactor = DEFAULT_I_FACTOR;
        this.reduceFactor = DEFAULT_R_FACTOR;
    }

    void calculateNextReviewDate() {
        if (lastReviewInUTC == null || currentIntervalDays == null) {
            return;
        }
        int intervalRounded = BigDecimal.valueOf(currentIntervalDays)
                .setScale(0, INTERVAL_ROUNDING_MODE).intValue();
        LocalDateTime newNextReviewDateInUTC = LocalDateTime.now(Clock.systemUTC()).plusDays(intervalRounded);
        this.nextReviewInUTC = newNextReviewDateInUTC/*.with(LocalTime.MIDNIGHT)*/;
        this.nextReviewWithoutTimeInUTC = nextReviewInUTC.toLocalDate();
    }

    void increaseIFactorByOneUnit() {
        increaseFactor += FACTOR_UPDATE_UNIT;
        if (increaseFactor > I_FACTOR_UPPER_LIMIT) {
            increaseFactor = I_FACTOR_UPPER_LIMIT;
        }
    }

    void decreaseIFactorByOneUnit() {
        increaseFactor -= FACTOR_UPDATE_UNIT;
        if (increaseFactor < I_FACTOR_BOTTOM_LIMIT) {
            increaseFactor = I_FACTOR_BOTTOM_LIMIT;
        }
    }

    void increaseRFactorByOneUnit() {
        reduceFactor += FACTOR_UPDATE_UNIT;
        if (reduceFactor > R_FACTOR_UPPER_LIMIT) {
            reduceFactor = R_FACTOR_UPPER_LIMIT;
        }
    }

    void decreaseRFactorByOneUnit() {
        reduceFactor -= FACTOR_UPDATE_UNIT;
        if (reduceFactor < R_FACTOR_BOTTOM_LIMIT) {
            reduceFactor = R_FACTOR_BOTTOM_LIMIT;
        }
    }
}
