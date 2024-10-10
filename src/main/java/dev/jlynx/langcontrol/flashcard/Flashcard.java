package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.spacedrepetition.BasicSpacedRepetitionAlgorithm;
import dev.jlynx.langcontrol.util.DateTimeTools;
import jakarta.persistence.*;
import dev.jlynx.langcontrol.deck.Deck;
import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.spacedrepetition.LearnModeStep;
import lombok.Getter;
import lombok.Setter;

import java.time.*;

@Getter @Setter
@Entity
@Table(name = "flashcard")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public class Flashcard {

    @Transient
    protected static final float INITIAL_IFACTOR = BasicSpacedRepetitionAlgorithm.INITIAL_IFACTOR;

    @Transient
    protected static final float INITIAL_RFACTOR = BasicSpacedRepetitionAlgorithm.INITIAL_RFACTOR;

    @Transient
    public static final long INITIAL_REVIEW_INTERVAL = BasicSpacedRepetitionAlgorithm.INITIAL_REVIEW_INTERVAL;

    @Transient
    public static final LearnModeStep INIT_LMS = BasicSpacedRepetitionAlgorithm.INIT_LMS;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "in_learn_mode", nullable = false)
    protected boolean inLearnMode;

    @Column(name = "learn_mode_step")
    protected LearnModeStep learnModeStep;

    /**
     * Card's next view datetime in UTC set by the spaced repetition algorithm.
     */
    @Column(name = "next_view_utc", columnDefinition = "DATETIME")
    protected LocalDateTime nextView;

    /**
     * Currently applied interval in minutes.
     */
    @Column(name = "current_interval")
    protected Long currentInterval;

    @Column(name = "increase_factor", nullable = false)
    protected Float iFactor;

    @Column(name = "reduce_factor", nullable = false)
    protected Float rFactor;

    /**
     * Card's creation datetime in UTC.
     */
    @Column(name = "created_at", columnDefinition = "DATETIME", nullable = false)
    protected LocalDateTime createdAt;

    /**
     * The language that the user knows.
     */
    @Column(name = "source_lang_code", columnDefinition = "VARCHAR(2)", nullable = false)
    protected LanguageCode sourceLang;

    /**
     * The language that the user is learning.
     */
    @Column(name = "target_lang_code", columnDefinition = "VARCHAR(2)", nullable = false)
    protected LanguageCode targetLang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deck_id", nullable = false, foreignKey = @ForeignKey(name = "fk_flashcard_deck"))
    protected Deck deck;

    protected Flashcard() { }

    protected Flashcard(Deck deck, LanguageCode sourceLang, LanguageCode targetLang, boolean initialReviewMode) {
        if (initialReviewMode) {
            this.inLearnMode = false;
            this.learnModeStep = null;
            this.currentInterval = INITIAL_REVIEW_INTERVAL;
        } else {
            this.inLearnMode = true;
            this.learnModeStep = INIT_LMS;
            this.currentInterval = Duration
                    .of(learnModeStep.getAmountToAdd(), learnModeStep.getTemporalUnit())
                    .toMinutes();
        }
        this.nextView = LocalDateTime.now(Clock.systemUTC()).plusMinutes(currentInterval);

        this.id = null;
        this.iFactor = INITIAL_IFACTOR;
        this.rFactor = INITIAL_RFACTOR;
        this.createdAt = new DateTimeTools().getNowUtc();
        this.deck = deck;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
    }

    /**
     * Updates the new current interval value and sets the {@code nextView} datetime accordingly.
     *
     * @param newInterval flashcard's new interval in minutes
     */
    public void updateNextView(long newInterval) {
        this.setCurrentInterval(newInterval);
        this.setNextView(new DateTimeTools().getNowUtc().plusMinutes(this.getCurrentInterval()));
    }
}
