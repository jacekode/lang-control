package langcontrol.app.spacedrepetition;

import langcontrol.app.deck.LanguageCode;
import langcontrol.app.flashcard.WordFlashcard;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BasicSpacedRepetitionAlgorithmTest {

    private static BasicSpacedRepetitionAlgorithm underTest;

    @BeforeAll
    static void beforeAll() {
        underTest = new BasicSpacedRepetitionAlgorithm();
    }

    public static Stream<Arguments> parametersWithAtLeastOneNull() {
        WordFlashcard cardNotNull = cardInInitialState();
        RatingType ratingNotNull = RatingType.LEARN_NEXT;
        return Stream.of(
                Arguments.of(cardNotNull, null),
                Arguments.of(null, ratingNotNull),
                Arguments.of(null, null)
        );
    }

    public static Stream<RatingType> allReviewRatingTypes() {
        return Stream.of(RatingType.REVIEW_CANNOT_SOLVE, RatingType.REVIEW_DIFFICULT,
                RatingType.REVIEW_NORMAL, RatingType.REVIEW_DIFFICULT);
    }

    public static Stream<RatingType> allLearnRatingTypes() {
        return Stream.of(RatingType.LEARN_PREVIOUS, RatingType.LEARN_NORMAL,
                RatingType.LEARN_NEXT, RatingType.LEARN_TO_REVIEW_MODE);
    }

    public static WordFlashcard cardInInitialState() {
        return WordFlashcard.inInitialLearnMode()
                .withFront("test front")
                .withBack("test back")
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.SPANISH)
                .build();
    }

    public static WordFlashcard cardInLearnModeStepTwo() {
        WordFlashcard flashcard = WordFlashcard.inInitialLearnMode()
                .withFront("test front")
                .withBack("test back")
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.SPANISH)
                .build();
        flashcard.setLearnModeStep(LearnModeStep.TWO);
        return flashcard;
    }

    public static WordFlashcard cardInLearnModeStepThree() {
        WordFlashcard flashcard = WordFlashcard.inInitialLearnMode()
                .withFront("test front")
                .withBack("test back")
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.SPANISH)
                .build();
        flashcard.setLearnModeStep(LearnModeStep.THREE);
        return flashcard;
    }

    public static Stream<WordFlashcard> allCardsInLearnModeFromTheBeginning() {
        return Stream.of(cardInInitialState(), cardInLearnModeStepTwo(), cardInLearnModeStepThree());
    }

    public static WordFlashcard cardInInitialReviewModeState() {
        return WordFlashcard.inInitialReviewMode()
                .withFront("test front")
                .withBack("test back")
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.SPANISH)
                .build();
    }

    public static WordFlashcard cardInReviewModeWithData() {
        WordFlashcard flashcard = cardInInitialReviewModeState();
        flashcard.setLastReviewInUTC(LocalDateTime
                .of(2022, 5, 23, 9, 23, 47, 1234));
        flashcard.setCurrentIntervalDays(5.4);
        flashcard.setNextReviewInUTC(flashcard.getLastReviewInUTC().plusDays(5));
        flashcard.setNextReviewWithoutTimeInUTC(flashcard.getNextReviewInUTC().toLocalDate());
        return flashcard;
    }

    public static WordFlashcard cardInReviewModeWithIntervalLessThanMinimum() {
        WordFlashcard flashcard = cardInInitialReviewModeState();
        flashcard.setLastReviewInUTC(LocalDateTime
                .of(2022, 5, 23, 9, 23, 47, 1234));
        flashcard.setCurrentIntervalDays(1.4);
        flashcard.setNextReviewInUTC(flashcard.getLastReviewInUTC().plusDays(1));
        flashcard.setNextReviewWithoutTimeInUTC(flashcard.getNextReviewInUTC().toLocalDate());
        return flashcard;
    }

    public static Stream<WordFlashcard> cardsInReviewModeWithIntervalDaysLessThanOrEqualToMinimum() {
        return Stream.of(cardInInitialReviewModeState(), cardInReviewModeWithIntervalLessThanMinimum());
    }

    public static Stream<WordFlashcard> allCardsInReviewMode() {
        WordFlashcard cardWithFactorsCloseToUpperLimit = cardInReviewModeWithData();
        cardWithFactorsCloseToUpperLimit.setiFactor(WordFlashcard.I_FACTOR_UPPER_LIMIT - 0.001);
        cardWithFactorsCloseToUpperLimit.setrFactor(WordFlashcard.R_FACTOR_UPPER_LIMIT - 0.001);

        WordFlashcard cardWithFactorsCloseToBottomLimit = cardInReviewModeWithData();
        cardWithFactorsCloseToBottomLimit.setiFactor(WordFlashcard.I_FACTOR_BOTTOM_LIMIT + 0.001);
        cardWithFactorsCloseToBottomLimit.setrFactor(WordFlashcard.R_FACTOR_BOTTOM_LIMIT + 0.001);

        return Stream.of(cardInInitialReviewModeState(), cardInReviewModeWithData(),
                cardInReviewModeWithIntervalLessThanMinimum(), cardWithFactorsCloseToUpperLimit,
                cardWithFactorsCloseToBottomLimit);
    }

    public static WordFlashcard cardInLearnModeWithReviewModeData() {
        WordFlashcard flashcard = cardInInitialState();
        flashcard.setLastReviewInUTC(LocalDateTime
                .of(2022, 5, 23, 9, 23, 47, 1234));
        flashcard.setCurrentIntervalDays(5.4);
        flashcard.setNextReviewInUTC(flashcard.getLastReviewInUTC().plusDays(5));
        flashcard.setNextReviewWithoutTimeInUTC(flashcard.getNextReviewInUTC().toLocalDate());
        return flashcard;
    }


    @ParameterizedTest
    @MethodSource("parametersWithAtLeastOneNull")
    void apply_ShouldThrowException_WhenAtLeastOneArgumentIsNull(WordFlashcard flashcard, RatingType rating) {
        // when
        Exception thrown = null;
        try {
            underTest.apply(flashcard, rating);
        } catch (Exception e) {
            thrown = e;
        }

        // then
        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    @ParameterizedTest
    @MethodSource("allReviewRatingTypes")
    void apply_ShouldThrowException_WhenCardIsInLearnModeAndRatingIsOfReviewMode(RatingType rating) {
        // given
        WordFlashcard testFlashcard = cardInInitialState();

        // when
        Exception thrown = null;
        try {
            underTest.apply(testFlashcard, rating);
        } catch (Exception e) {
            thrown = e;
        }

        // then
        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalStateException);
    }

    @ParameterizedTest
    @MethodSource("allLearnRatingTypes")
    void apply_ShouldThrowException_WhenCardIsInReviewModeAndRatingIsOfReviewMode(RatingType rating) {
        // given
        WordFlashcard testFlashcard = cardInInitialState();

        // when
        underTest.apply(testFlashcard, RatingType.LEARN_TO_REVIEW_MODE);
        Exception thrown = null;
        try {
            underTest.apply(testFlashcard, rating);
        } catch (Exception e) {
            thrown = e;
        }

        // then
        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalStateException);
    }

    @Test
    void apply_ShouldNotChangeLearnViewDateTime_WhenLearnModeStepIsOneAndRatingIsLearnPrevious() {
        // given
        WordFlashcard testFlashcard = cardInInitialState();
        RatingType ratingPrevious = RatingType.LEARN_PREVIOUS;
        LocalDateTime expectedLearnViewAfterOp = LocalDateTime
                .of(2017, 5, 27, 11, 46, 23);

        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(expectedLearnViewAfterOp);

        // when
            underTest.apply(testFlashcard, ratingPrevious);
        }

        // then
        assertEquals(expectedLearnViewAfterOp, testFlashcard.getNextView());
    }

    @Test
    void apply_ShouldSetLearnViewInOneMinuteFromNow_WhenLearnModeStepIsOneAndRatingIsLearnNormal() {
        // given
        WordFlashcard testFlashcard = cardInInitialState();
        RatingType ratingNormal = RatingType.LEARN_NORMAL;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDateTime nextLearnViewAfterOpExpected = nowLDTForMock.plusMinutes(1);
        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingNormal);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertEquals(nextLearnViewAfterOpExpected, testFlashcard.getNextView());
        assertEquals(LearnModeStep.ONE, testFlashcard.getLearnModeStep());
    }

    @Test
    void apply_ShouldIncreaseLearnModeStepAndSetLearnViewInTenMinutesFromNow_WhenLearnModeStepIsOneAndRatingIsLearnNext() {
        // given
        WordFlashcard testFlashcard = cardInInitialState();
        RatingType ratingNext = RatingType.LEARN_NEXT;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDateTime nextLearnViewAfterOpExpected = nowLDTForMock.plusMinutes(10);
        try (MockedStatic<LocalDateTime> mockedStaticLCD = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLCD.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingNext);

            // then
            mockedStaticLCD.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertEquals(nextLearnViewAfterOpExpected, testFlashcard.getNextView());
        assertEquals(LearnModeStep.TWO, testFlashcard.getLearnModeStep());
    }

    @ParameterizedTest
    @MethodSource("allCardsInLearnModeFromTheBeginning")
    void apply_ShouldSwitchToReviewModeAndSetReviewInTwoDaysFromNow_WhenCardWasInLearnModeFromTheBeginning(WordFlashcard testFlashcard) {
        // given
        RatingType ratingToReviewMode = RatingType.LEARN_TO_REVIEW_MODE;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDateTime nextReviewAfterOpExpected = nowLDTForMock.plusDays(2);
        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingToReviewMode);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertFalse(testFlashcard.isInLearnMode());
        assertEquals(nextReviewAfterOpExpected, testFlashcard.getNextReviewInUTC());
        assertEquals(nextReviewAfterOpExpected.toLocalDate(), testFlashcard.getNextReviewWithoutTimeInUTC());
    }

    @Test
    void apply_ShouldDecreaseLearnModeStepAndSetLearnViewInOneMinuteFromNow_WhenLearnModeStepIsTwoAndRatingIsLearnPrevious() {
        // given
        WordFlashcard testFlashcard = cardInLearnModeStepTwo();
        RatingType ratingPrevious = RatingType.LEARN_PREVIOUS;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDateTime nextLearnViewAfterOpExpected = nowLDTForMock.plusMinutes(1);
        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingPrevious);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertEquals(nextLearnViewAfterOpExpected, testFlashcard.getNextView());
        assertEquals(LearnModeStep.ONE, testFlashcard.getLearnModeStep());
    }

    @Test
    void apply_ShouldSetLearnViewInTenMinutesFromNow_WhenLearnModeStepIsTwoAndRatingIsLearnNormal() {
        // given
        WordFlashcard testFlashcard = cardInLearnModeStepTwo();
        RatingType ratingNormal = RatingType.LEARN_NORMAL;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDateTime nextLearnViewAfterOpExpected = nowLDTForMock.plusMinutes(10);
        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingNormal);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertEquals(nextLearnViewAfterOpExpected, testFlashcard.getNextView());
        assertEquals(LearnModeStep.TWO, testFlashcard.getLearnModeStep());
    }

    @Test
    void apply_ShouldIncreaseLearnModeStepAndSetLearnViewInOneDayFromNow_WhenLearnModeStepIsTwoAndRatingIsLearnNext() {
        // given
        WordFlashcard testFlashcard = cardInLearnModeStepTwo();
        RatingType ratingNext = RatingType.LEARN_NEXT;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDateTime nextLearnViewAfterOpExpected = nowLDTForMock.plusDays(1);
        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingNext);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertEquals(nextLearnViewAfterOpExpected, testFlashcard.getNextView());
        assertEquals(LearnModeStep.THREE, testFlashcard.getLearnModeStep());
    }

    @Test
    void apply_ShouldDecreaseLearnModeStepAndSetLearnViewInTenMinutesFromNow_WhenLearnModeStepIsThreeAndRatingIsLearnPrevious() {
        // given
        WordFlashcard testFlashcard = cardInLearnModeStepThree();
        RatingType ratingPrevious = RatingType.LEARN_PREVIOUS;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDateTime nextLearnViewAfterOpExpected = nowLDTForMock.plusMinutes(10);
        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingPrevious);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertEquals(nextLearnViewAfterOpExpected, testFlashcard.getNextView());
        assertEquals(LearnModeStep.TWO, testFlashcard.getLearnModeStep());
    }

    @Test
    void apply_ShouldSetLearnViewInOneDayFromNow_WhenLearnModeStepIsThreeAndRatingIsLearnNormal() {
        // given
        WordFlashcard testFlashcard = cardInLearnModeStepThree();
        RatingType ratingNormal = RatingType.LEARN_NORMAL;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDateTime nextLearnViewAfterOpExpected = nowLDTForMock.plusDays(1);
        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingNormal);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertEquals(nextLearnViewAfterOpExpected, testFlashcard.getNextView());
        assertEquals(LearnModeStep.THREE, testFlashcard.getLearnModeStep());
    }

    @Test
    void apply_ShouldSwitchToReviewModeAndSetReviewInTwoDaysFromNow_WhenLearnModeStepIsThreeAndRatingIsLearnNext() {
        // given
        WordFlashcard testFlashcard = cardInLearnModeStepThree();
        RatingType ratingNormal = RatingType.LEARN_NEXT;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDateTime nextReviewAfterOpExpected = nowLDTForMock.plusDays(2);
        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingNormal);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertFalse(testFlashcard.isInLearnMode());
        assertEquals(nextReviewAfterOpExpected, testFlashcard.getNextReviewInUTC());
        assertEquals(nextReviewAfterOpExpected.toLocalDate(), testFlashcard.getNextReviewWithoutTimeInUTC());
    }

    @Test
    void apply_ShouldUseThePreviouslySavedIntervalDays_WhenCardIsInLearnModeWithReviewModeDataAndRatingIsLearnToReviewMode() {
        // given
        WordFlashcard testFlashcard = cardInLearnModeWithReviewModeData();
        double intervalDaysBeforeOp = testFlashcard.getCurrentIntervalDays();
        RatingType ratingToReviewMode = RatingType.LEARN_TO_REVIEW_MODE;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDate nextReviewWithoutTimeAfterOpExpected = nowLDTForMock.plusDays(5).toLocalDate();
        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingToReviewMode);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertFalse(testFlashcard.isInLearnMode());
        assertEquals(nextReviewWithoutTimeAfterOpExpected, testFlashcard.getNextReviewWithoutTimeInUTC());
        assertEquals(intervalDaysBeforeOp, testFlashcard.getCurrentIntervalDays());
    }

    @ParameterizedTest
    @MethodSource("cardsInReviewModeWithIntervalDaysLessThanOrEqualToMinimum")
    void apply_ShouldSwitchToLearnModeAndSetLearnViewInOneMinuteFromNow_WhenIntervalIsLessThanOrEqualToMinimumAndRatingIsReviewCannotSolve(WordFlashcard testFlashcard) {
        // given
        RatingType ratingCannotSolve = RatingType.REVIEW_CANNOT_SOLVE;
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);
        LocalDateTime nextLearnViewAfterOpExpected = nowLDTForMock.plusMinutes(1);
        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingCannotSolve);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()));
        }
        assertTrue(testFlashcard.isInLearnMode());
        assertEquals(nextLearnViewAfterOpExpected, testFlashcard.getNextView());
    }

    @Test
    void apply_ShouldSetIntervalDaysToMinimumAndNotChangeFactors_WhenIntervalIsMoreThanMinimumAndRatingIsReviewCannotSolve() {
        // given
        WordFlashcard testFlashcard = cardInReviewModeWithData();
        RatingType ratingCannotSolve = RatingType.REVIEW_CANNOT_SOLVE;
        double rFactorBeforeOp = testFlashcard.getrFactor();
        double iFactorBeforeOp = testFlashcard.getiFactor();

        // when
        underTest.apply(testFlashcard, ratingCannotSolve);

        // then
        assertEquals(2.0, testFlashcard.getCurrentIntervalDays());
        assertEquals(iFactorBeforeOp, testFlashcard.getiFactor());
        assertEquals(rFactorBeforeOp, testFlashcard.getrFactor());
    }

    @ParameterizedTest
    @MethodSource("allCardsInReviewMode")
    void apply_ShouldDecreaseIntervalAndFactorsAndSetNextReviewAccordingly_WhenRatingIsReviewDifficult(WordFlashcard testFlashcard) {
        // given
        RatingType ratingDifficult = RatingType.REVIEW_DIFFICULT;
        double factorUpdateUnit = WordFlashcard.FACTOR_UPDATE_UNIT;
        double rFactorBottomLimit = WordFlashcard.R_FACTOR_BOTTOM_LIMIT;
        double iFactorBottomLimit = WordFlashcard.I_FACTOR_BOTTOM_LIMIT;
        double intervalDaysBeforeOp = testFlashcard.getCurrentIntervalDays();
        double rFactorBeforeOp = testFlashcard.getrFactor();
        double iFactorBeforeOp = testFlashcard.getiFactor();
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);

        double intervalDaysAfterOpExpected = intervalDaysBeforeOp * rFactorBeforeOp;
        double rFactorAfterOpExpected = Math.max(rFactorBeforeOp - factorUpdateUnit, rFactorBottomLimit);
        double iFactorAfterOpExpected = Math.max(iFactorBeforeOp - factorUpdateUnit, iFactorBottomLimit);
        LocalDateTime nextReviewAfterOpExpected = nowLDTForMock.plusDays(BigDecimal
                .valueOf(intervalDaysAfterOpExpected)
                .setScale(0, RoundingMode.HALF_DOWN)
                .intValue());

        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingDifficult);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()), Mockito.atLeastOnce());
        }
        assertEquals(intervalDaysAfterOpExpected, testFlashcard.getCurrentIntervalDays());
        assertEquals(rFactorAfterOpExpected, testFlashcard.getrFactor());
        assertEquals(iFactorAfterOpExpected, testFlashcard.getiFactor());
        assertEquals(nextReviewAfterOpExpected, testFlashcard.getNextReviewInUTC());
        assertEquals(nextReviewAfterOpExpected.toLocalDate(), testFlashcard.getNextReviewWithoutTimeInUTC());
    }

    @ParameterizedTest
    @MethodSource("allCardsInReviewMode")
    void apply_ShouldIncreaseIntervalDaysSlightlyAndNotChangeFactorsAndSetNextReviewAccordingly_WhenRatingIsReviewNormal(WordFlashcard testFlashcard) {
        // given
        RatingType ratingNormal = RatingType.REVIEW_NORMAL;
        double intervalDaysBeforeOp = testFlashcard.getCurrentIntervalDays();
        double rFactorBeforeOp = testFlashcard.getrFactor();
        double iFactorBeforeOp = testFlashcard.getiFactor();
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);

        double intervalDaysAfterOpExpected = intervalDaysBeforeOp * 1.1;
        LocalDateTime nextReviewAfterOpExpected = nowLDTForMock.plusDays(BigDecimal
                .valueOf(intervalDaysAfterOpExpected)
                .setScale(0, RoundingMode.HALF_DOWN)
                .intValue());

        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingNormal);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()), Mockito.atLeastOnce());
        }
        assertEquals(intervalDaysAfterOpExpected, testFlashcard.getCurrentIntervalDays());
        assertEquals(rFactorBeforeOp, testFlashcard.getrFactor());
        assertEquals(iFactorBeforeOp, testFlashcard.getiFactor());
        assertEquals(nextReviewAfterOpExpected, testFlashcard.getNextReviewInUTC());
        assertEquals(nextReviewAfterOpExpected.toLocalDate(), testFlashcard.getNextReviewWithoutTimeInUTC());
    }

    @ParameterizedTest
    @MethodSource("allCardsInReviewMode")
    void apply_ShouldIncreaseIntervalDaysByIFactorAndIncreaseFactorsAndSetNextReviewAccordingly_WhenRatingIsReviewEasy(WordFlashcard testFlashcard) {
        // given
        RatingType ratingEasy = RatingType.REVIEW_EASY;
        double factorUpdateUnit = WordFlashcard.FACTOR_UPDATE_UNIT;
        double rFactorUpperLimit = WordFlashcard.R_FACTOR_UPPER_LIMIT;
        double iFactorUpperLimit = WordFlashcard.I_FACTOR_UPPER_LIMIT;
        double intervalDaysBeforeOp = testFlashcard.getCurrentIntervalDays();
        double rFactorBeforeOp = testFlashcard.getrFactor();
        double iFactorBeforeOp = testFlashcard.getiFactor();
        LocalDateTime nowLDTForMock = LocalDateTime
                .of(2022, 5, 11, 9, 23, 47, 1234);

        double intervalDaysAfterOpExpected = intervalDaysBeforeOp * iFactorBeforeOp;
        double rFactorAfterOpExpected = Math.min(rFactorBeforeOp + factorUpdateUnit, rFactorUpperLimit);
        double iFactorAfterOpExpected = Math.min(iFactorBeforeOp + factorUpdateUnit, iFactorUpperLimit);
        LocalDateTime nextReviewAfterOpExpected = nowLDTForMock.plusDays(BigDecimal
                .valueOf(intervalDaysAfterOpExpected)
                .setScale(0, RoundingMode.HALF_DOWN)
                .intValue());

        try (MockedStatic<LocalDateTime> mockedStaticLDT = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStaticLDT.when(() -> LocalDateTime.now(Clock.systemUTC())).thenReturn(nowLDTForMock);

            // when
            underTest.apply(testFlashcard, ratingEasy);

            // then
            mockedStaticLDT.verify(() -> LocalDateTime.now(Clock.systemUTC()), Mockito.atLeastOnce());
        }
        assertEquals(intervalDaysAfterOpExpected, testFlashcard.getCurrentIntervalDays());
        assertEquals(rFactorAfterOpExpected, testFlashcard.getrFactor());
        assertEquals(iFactorAfterOpExpected, testFlashcard.getiFactor());
        assertEquals(nextReviewAfterOpExpected, testFlashcard.getNextReviewInUTC());
        assertEquals(nextReviewAfterOpExpected.toLocalDate(), testFlashcard.getNextReviewWithoutTimeInUTC());
    }
}