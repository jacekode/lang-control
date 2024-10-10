package dev.jlynx.langcontrol.spacedrepetition;

import dev.jlynx.langcontrol.flashcard.WordFlashcard;
import dev.jlynx.langcontrol.flashcard.WordFlashcardService;
import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SpacedRepetitionServiceImplTest {

    @InjectMocks
    private SpacedRepetitionServiceImpl underTest;

    @Mock
    private WordFlashcardService mockedWordFlashcardService;
    @Mock
    private SpacedRepetitionAlgorithm mockedSpacedRepetitionAlgorithm;

    @Captor
    ArgumentCaptor<Long> longCaptor;
    @Captor
    ArgumentCaptor<WordFlashcard> wordFlashcardCaptor;
    @Captor
    ArgumentCaptor<RatingType> ratingTypeCaptor;

    @ParameterizedTest
    @MethodSource("invalidArguments")
    void applyRating_ShouldThrow_WhenArgumentsAreNull(Long flashcardId, RatingType ratingType) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.applyRating(flashcardId, ratingType);
        } catch (Exception e) {
            thrown = e;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void applyRating_ShouldSwitchToReviewMode() {
        // given
        RatingType rating = RatingType.REVIEW_PARTIALLY;
        Long cardId = 2L;
//        WordFlashcard card = WordFlashcard.inInitialLearnMode()
//                .withTranslatedWord("translation")
//                .withTargetWord("target")
//                .withSourceLang(LanguageCode.ENGLISH)
//                .withTargetLang(LanguageCode.SPANISH)
//                .build();
//        card.setId(cardId);
//        Deck deck = new Deck(76L, "test deck", null,
//                LanguageCode.ENGLISH, LanguageCode.DANISH, new ArrayList<>());
//        deck.getFlashcards().add(card);

        WordFlashcard mockedCard = Mockito.mock(WordFlashcard.class);
        given(mockedWordFlashcardService.getCardById(cardId)).willReturn(mockedCard);
        given(mockedCard.getId()).willReturn(cardId);
        given(mockedCard.isInLearnMode()).willReturn(true, false);

        // when
        FlashcardRatingResponse returned = underTest.applyRating(cardId, rating);

        // then
        InOrder inOrder = Mockito.inOrder(mockedSpacedRepetitionAlgorithm, mockedCard, mockedWordFlashcardService);

        then(mockedWordFlashcardService).should(inOrder).getCardById(longCaptor.capture());
        then(mockedCard).should(inOrder).isInLearnMode();
        then(mockedSpacedRepetitionAlgorithm).should(inOrder).apply(wordFlashcardCaptor.capture(), ratingTypeCaptor.capture());
        then(mockedCard).should(inOrder).isInLearnMode();

        assertEquals(cardId, longCaptor.getValue());
        assertEquals(cardId, wordFlashcardCaptor.getValue().getId());
        assertEquals(rating, ratingTypeCaptor.getValue());

        assertEquals(cardId, returned.id());
        assertTrue(returned.switchedToReviewMode());
        assertFalse(returned.switchedToLearnMode());
    }

    @Test
    void applyRating_ShouldSwitchToLearnMode() {
        // given
        RatingType rating = RatingType.REVIEW_PARTIALLY;
        Long cardId = 2L;
        WordFlashcard mockedCard = Mockito.mock(WordFlashcard.class);
        given(mockedWordFlashcardService.getCardById(cardId)).willReturn(mockedCard);
        given(mockedCard.getId()).willReturn(cardId);
        given(mockedCard.isInLearnMode()).willReturn(false, true);

        // when
        FlashcardRatingResponse returned = underTest.applyRating(cardId, rating);

        // then
        InOrder inOrder = Mockito.inOrder(mockedSpacedRepetitionAlgorithm, mockedCard, mockedWordFlashcardService);

        then(mockedWordFlashcardService).should(inOrder).getCardById(longCaptor.capture());
        then(mockedCard).should(inOrder).isInLearnMode();
        then(mockedSpacedRepetitionAlgorithm).should(inOrder).apply(wordFlashcardCaptor.capture(), ratingTypeCaptor.capture());
        then(mockedCard).should(inOrder).isInLearnMode();

        assertEquals(cardId, longCaptor.getValue());
        assertEquals(cardId, wordFlashcardCaptor.getValue().getId());
        assertEquals(rating, ratingTypeCaptor.getValue());

        assertEquals(cardId, returned.id());
        assertFalse(returned.switchedToReviewMode());
        assertTrue(returned.switchedToLearnMode());
    }


    static Stream<Arguments> invalidArguments() {
        return Stream.of(
                Arguments.of(null, RatingType.LEARN_DONT_KNOW),
                Arguments.of(5L, null),
                Arguments.of(null, null)
        );
    }
}