package langcontrol.app.spaced_repetition;

import langcontrol.app.deck.LanguageCode;
import langcontrol.app.flashcard.Flashcard;
import langcontrol.app.flashcard.FlashcardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class SpacedRepetitionServiceImplTest {

    private final SpacedRepetitionServiceImpl underTest;
    private final FlashcardRepository mockedFlashcardRepository;
    private final SpacedRepetition mockedSpacedRepetitionAlgorithm;

    public SpacedRepetitionServiceImplTest() {
        this.mockedFlashcardRepository = Mockito.mock(FlashcardRepository.class);
        this.mockedSpacedRepetitionAlgorithm = Mockito.mock(SpacedRepetition.class);
        this.underTest = new SpacedRepetitionServiceImpl(mockedFlashcardRepository,
                flashcardService, mockedSpacedRepetitionAlgorithm);
    }

    public static Stream<Arguments> parametersWithNullValues() {
        return Stream.of(
                Arguments.arguments(null, RatingType.LEARN_NEXT),
                Arguments.arguments(1L, null),
                Arguments.arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersWithNullValues")
    void applyRating_ShouldThrowException_WhenParametersAreNull(Long flashcardId, RatingType ratingType) {
        // when
        Exception thrown = null;
        try {
            underTest.applyRating(flashcardId, ratingType);
        } catch (Exception e) {
            thrown = e;
        }

        // then
        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    @Test
    void applyRating_ShouldInvokeApplyMethodOfAlgorithmClassWithCorrectParameters_WhenParametersAreNotNull() {
        // given
        Flashcard flashcardForMock = Flashcard.inInitialLearnModeState()
                .front("test front")
                .back("test back")
                .sourceLanguage(LanguageCode.ENGLISH)
                .targetLanguage(LanguageCode.SPANISH)
                .build();
        flashcardForMock.setId(2L);
        Long flashcardIdParam = 2L;
        RatingType ratingTypeParam = RatingType.REVIEW_NORMAL;
        given(mockedFlashcardRepository.findById(2L)).willReturn(Optional.of(flashcardForMock));

        // when
        underTest.applyRating(flashcardIdParam, ratingTypeParam);

        // then
        ArgumentCaptor<Long> argCaptorForLong = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Flashcard> argCaptorForFlashcard = ArgumentCaptor.forClass(Flashcard.class);
        ArgumentCaptor<RatingType> argCaptorForRatingType = ArgumentCaptor.forClass(RatingType.class);
        InOrder inOrder = Mockito.inOrder(mockedFlashcardRepository, mockedSpacedRepetitionAlgorithm);

        then(mockedFlashcardRepository).should(inOrder).findById(argCaptorForLong.capture());
        then(mockedSpacedRepetitionAlgorithm).should(inOrder).apply(argCaptorForFlashcard.capture(),
                argCaptorForRatingType.capture());
        assertEquals(flashcardIdParam, argCaptorForLong.getValue());
        assertEquals(flashcardIdParam, argCaptorForFlashcard.getValue().getId());
        assertEquals(ratingTypeParam, argCaptorForRatingType.getValue());
    }
}