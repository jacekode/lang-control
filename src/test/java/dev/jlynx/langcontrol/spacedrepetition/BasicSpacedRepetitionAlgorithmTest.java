package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.flashcard.WordFlashcard;
import dev.jlynx.langcontrol.util.DateTimeTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.LongFunction;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class BasicSpacedRepetitionAlgorithmTest {

    private BasicSpacedRepetitionAlgorithm underTest;

    private DateTimeTools mockDtt;

    @BeforeEach
    void setUp() {
        mockDtt = Mockito.mock(DateTimeTools.class);
        underTest = new BasicSpacedRepetitionAlgorithm(mockDtt);
    }


    @ParameterizedTest
    @MethodSource("paramsWithNullValues")
    void apply_ShouldThrow_WhenArgsContainNull(WordFlashcard flashcard, RatingType rating) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.apply(flashcard, rating);
        } catch (Exception e) {
            thrown = e;
        }

        // then
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    @ParameterizedTest
    @MethodSource("reviewRatingTypes")
    void apply_ShouldThrow_WhenCardIsInLearnModeAndRatingIsForReviewMode(RatingType reviewRating) {
        // given
        WordFlashcard learnModeCard = cardInInitialLearnMode();
        Exception thrown = null;

        // when
        try {
            underTest.apply(learnModeCard, reviewRating);
        } catch (Exception e) {
            thrown = e;
        }

        // then
        assertTrue(thrown instanceof IllegalStateException);
    }

    @ParameterizedTest
    @MethodSource("learnRatingTypes")
    void apply_ShouldThrow_WhenCardIsInReviewModeAndRatingIsForLearnMode(RatingType learnRating) {
        // given
        WordFlashcard reviewModeCard = cardInInitialReviewMode();
        Exception thrown = null;

        // when
        try {
            underTest.apply(reviewModeCard, learnRating);
        } catch (Exception e) {
            thrown = e;
        }

        // then
        assertTrue(thrown instanceof IllegalStateException);
    }

    @Test
    void apply_ShouldKeepLearnModeStep_WhenLearnModeStepIsOneAndRatingIsLearnDontKnow() {
        // given
        WordFlashcard stepOneFlashcard = cardInLearnModeStepOne();
        RatingType rating = RatingType.LEARN_DONT_KNOW;
        LocalDateTime ldtNow = LocalDateTime.of(2017, 5, 27, 11, 46, 23);
        LocalDateTime expectedNextView = ldtNow.plusMinutes(1);
        given(mockDtt.getNowUtc()).willReturn(ldtNow);

        // when
        underTest.apply(stepOneFlashcard, rating);

        // then
        then(mockDtt).should().getNowUtc();
        assertEquals(expectedNextView, stepOneFlashcard.getNextView());
        assertEquals(1, stepOneFlashcard.getCurrentInterval());
        assertEquals(LearnModeStep.ONE, stepOneFlashcard.getLearnModeStep());
        assertTrue(stepOneFlashcard.isInLearnMode());
    }

    @Test
    void apply_ShouldSetLearnModeStepToTwo_WhenLearnModeStepIsOneAndRatingIsLearnKnow() {
        // given
        WordFlashcard stepOneFlashcard = cardInLearnModeStepOne();
        RatingType rating = RatingType.LEARN_KNOW;
        LocalDateTime ldtNow = LocalDateTime.of(2017, 5, 27, 11, 46, 23, 1234);
        LocalDateTime expectedNextView = ldtNow.plusMinutes(10);
        given(mockDtt.getNowUtc()).willReturn(ldtNow);

        // when
        underTest.apply(stepOneFlashcard, rating);

        // then
        then(mockDtt).should().getNowUtc();
        assertEquals(expectedNextView, stepOneFlashcard.getNextView());
        assertEquals(10, stepOneFlashcard.getCurrentInterval());
        assertEquals(LearnModeStep.TWO, stepOneFlashcard.getLearnModeStep());
        assertTrue(stepOneFlashcard.isInLearnMode());
    }

    @Test
    void apply_ShouldSetLearnModeStepToOne_WhenLearnModeStepIsTwoAndRatingIsLearnDontKnow() {
        // given
        WordFlashcard stepTwoflashcard = cardInInitialLearnMode();
        RatingType rating = RatingType.LEARN_DONT_KNOW;
        LocalDateTime ldtNow = LocalDateTime.of(2017, 5, 27, 11, 46, 23, 1234);
        LocalDateTime expectedNextView = ldtNow.plusMinutes(1);
        given(mockDtt.getNowUtc()).willReturn(ldtNow);

        // when
        underTest.apply(stepTwoflashcard, rating);

        // then
        then(mockDtt).should().getNowUtc();
        assertEquals(expectedNextView, stepTwoflashcard.getNextView());
        assertEquals(LearnModeStep.ONE, stepTwoflashcard.getLearnModeStep());
        assertTrue(stepTwoflashcard.isInLearnMode());
    }

    @Test
    void apply_ShouldSwitchToReviewModeAndSetNextViewIn24Hours_WhenLearnModeStepIsTwoAndRatingIsLearnKnow() {
        // given
        WordFlashcard stepTwoflashcard = cardInInitialLearnMode();
        RatingType rating = RatingType.LEARN_KNOW;
        LocalDateTime ldtNow = LocalDateTime.of(2017, 5, 27, 11, 46, 23, 1234);
        LocalDateTime expectedNextView = ldtNow.plusHours(24);
        given(mockDtt.getNowUtc()).willReturn(ldtNow);

        // when
        underTest.apply(stepTwoflashcard, rating);

        // then
        then(mockDtt).should().getNowUtc();
        assertFalse(stepTwoflashcard.isInLearnMode());
        assertEquals(expectedNextView, stepTwoflashcard.getNextView());
    }

    @ParameterizedTest
    @MethodSource("reviewModeCards")
    void apply_ShouldIncreaseIntervalAndFactor_WhenRatingIsReviewRemember(WordFlashcard flashcard) {
        // given
        RatingType rating = RatingType.REVIEW_REMEMBER;
        LocalDateTime ldtNow = LocalDateTime.of(2022, 5, 11, 9, 23, 47, 1234);
        Long intervalBefore = flashcard.getCurrentInterval();
        Float iFactorBefore = flashcard.getIFactor();
        Float rFactorBefore = flashcard.getRFactor();
        long expectedInterval = (long) (intervalBefore * iFactorBefore);
        given(mockDtt.getNowUtc()).willReturn(ldtNow);

        // when
        underTest.apply(flashcard, rating);

        // then
        then(mockDtt).should().getNowUtc();
        assertEquals(expectedInterval, flashcard.getCurrentInterval());
        assertEquals(ldtNow.plusMinutes(expectedInterval), flashcard.getNextView());
        assertTrue(iFactorBefore <= flashcard.getIFactor() &&
                flashcard.getIFactor() <= BasicSpacedRepetitionAlgorithm.I_FACTOR_UPPER_LIMIT);
        assertTrue(rFactorBefore <= flashcard.getRFactor() &&
                flashcard.getRFactor() <= BasicSpacedRepetitionAlgorithm.R_FACTOR_UPPER_LIMIT);
    }

    @ParameterizedTest
    @MethodSource("reviewModeCards")
    void apply_ShouldHalveInterval_WhenRatingIsReviewPartially(WordFlashcard flashcard) {
        // given
        RatingType rating = RatingType.REVIEW_PARTIALLY;
        LocalDateTime ldtNow = LocalDateTime.of(2022, 5, 11, 9, 23, 47, 1234);
        Long intervalBefore = flashcard.getCurrentInterval();
        Float iFactorBefore = flashcard.getIFactor();
        Float rFactorBefore = flashcard.getRFactor();
        long expectedInterval = intervalBefore / 2;
        given(mockDtt.getNowUtc()).willReturn(ldtNow);

        // when
        underTest.apply(flashcard, rating);

        // then
        then(mockDtt).should().getNowUtc();
        assertEquals(expectedInterval, flashcard.getCurrentInterval());
        assertEquals(ldtNow.plusMinutes(expectedInterval), flashcard.getNextView());
        assertEquals(iFactorBefore, flashcard.getIFactor());
        assertEquals(rFactorBefore, flashcard.getRFactor());
    }

    @ParameterizedTest
    @MethodSource("reviewModeCards")
    void apply_ShouldDecreaseIntervalAndFactors_WhenRatingIsReviewForgot(WordFlashcard flashcard) {
        // given
        RatingType rating = RatingType.REVIEW_FORGOT;
        LocalDateTime ldtNow = LocalDateTime.of(2022, 5, 11, 9, 23, 47, 1234);
        Long intervalBefore = flashcard.getCurrentInterval();
        Float iFactorBefore = flashcard.getIFactor();
        Float rFactorBefore = flashcard.getRFactor();
        long expectedInterval = (long) (intervalBefore * rFactorBefore);
        given(mockDtt.getNowUtc()).willReturn(ldtNow);

        // when
        underTest.apply(flashcard, rating);

        // then
        then(mockDtt).should().getNowUtc();
        assertEquals(expectedInterval, flashcard.getCurrentInterval());
        assertEquals(ldtNow.plusMinutes(expectedInterval), flashcard.getNextView());
        assertTrue(iFactorBefore >= flashcard.getIFactor() &&
                flashcard.getIFactor() >= BasicSpacedRepetitionAlgorithm.I_FACTOR_BOTTOM_LIMIT);
        assertTrue(rFactorBefore >= flashcard.getRFactor() &&
                flashcard.getRFactor() >= BasicSpacedRepetitionAlgorithm.R_FACTOR_BOTTOM_LIMIT);
    }


    public static Stream<Arguments> paramsWithNullValues() {
        WordFlashcard cardNotNull = cardInInitialLearnMode();
        RatingType ratingNotNull = RatingType.LEARN_KNOW;
        return Stream.of(
                Arguments.of(cardNotNull, null),
                Arguments.of(null, ratingNotNull),
                Arguments.of(null, null)
        );
    }

    public static Stream<RatingType> reviewRatingTypes() {
        return Stream.of(
                RatingType.REVIEW_REMEMBER,
                RatingType.REVIEW_PARTIALLY,
                RatingType.REVIEW_FORGOT
        );
    }

    public static Stream<RatingType> learnRatingTypes() {
        return Stream.of(
                RatingType.LEARN_KNOW,
                RatingType.LEARN_DONT_KNOW
        );
    }

    /**
     * Returns a {@code WordFlashcard} object in initial state (LearnModeStep.TWO).
     * @return a {@code WordFlashcard} object in initial state
     */
    public static WordFlashcard cardInInitialLearnMode() {
        return WordFlashcard.inInitialLearnMode()
                .withTranslatedWord("translation")
                .withTargetWord("target")
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.SPANISH)
                .build();
    }

    public static WordFlashcard cardInLearnModeStepOne() {
        WordFlashcard flashcard = cardInInitialLearnMode();
        flashcard.setLearnModeStep(LearnModeStep.ONE);
        return flashcard;
    }

    public static WordFlashcard cardInInitialReviewMode() {
        return WordFlashcard.inInitialReviewMode()
                .withTranslatedWord("translation")
                .withTargetWord("target")
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.SPANISH)
                .build();
    }

    public static Stream<WordFlashcard> cardsInReviewModeWithHigherInterval() {
        LongFunction<WordFlashcard> cardWithDaysInterval = days -> {
            WordFlashcard flashcard = cardInInitialReviewMode();
            long interval = Duration.ofDays(12).toMinutes();
            flashcard.setCurrentInterval(interval);
            return flashcard;
        };
        return Stream.of(9, 10, 11, 12, 13, 14, 15, 36, 71, 143)
                .map(cardWithDaysInterval::apply);
    }

    public static Stream<WordFlashcard> reviewModeCards() {
        return Stream.concat(
                Stream.of(cardInInitialReviewMode()),
                cardsInReviewModeWithHigherInterval()
        );
    }
}