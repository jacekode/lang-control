package langcontrol.app.spaced_repetition;

import langcontrol.app.account.Account;
import langcontrol.app.deck.Deck;
import langcontrol.app.deck.LanguageCode;
import langcontrol.app.flashcard.Flashcard;
import langcontrol.app.flashcard.FlashcardRepository;
import langcontrol.app.flashcard.FlashcardService;
import langcontrol.app.security.DefinedRoleValue;
import langcontrol.app.security.Role;
import langcontrol.app.user_profile.UserProfile;
import langcontrol.app.util.PrincipalRetriever;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class SpacedRepetitionServiceImplTest {

    private final SpacedRepetitionServiceImpl underTest;
    private final FlashcardService mockedFlashcardService;
    private final SpacedRepetition mockedSpacedRepetitionAlgorithm;

    public SpacedRepetitionServiceImplTest() {
        this.mockedSpacedRepetitionAlgorithm = Mockito.mock(SpacedRepetition.class);
        this.mockedFlashcardService = Mockito.mock(FlashcardService.class);
        this.underTest = new SpacedRepetitionServiceImpl(mockedFlashcardService, mockedSpacedRepetitionAlgorithm);
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
        RatingType ratingTypeParam = RatingType.REVIEW_NORMAL;
        Long testCardId = 2L;
        Flashcard testCard = Flashcard.inInitialLearnModeState()
                .front("test front")
                .back("test back")
                .sourceLanguage(LanguageCode.ENGLISH)
                .targetLanguage(LanguageCode.SPANISH)
                .build();
        testCard.setId(testCardId);

        Account testAccount = new Account(15L, "username", "testPassword",
                List.of(new Role(1L, DefinedRoleValue.USER)));
        UserProfile testUserProfile = new UserProfile(45L, "name");
        testUserProfile.setAccount(testAccount);
        testAccount.setUserProfile(testUserProfile);
        testUserProfile.setDecks(new ArrayList<>());
        Deck testDeck = new Deck(76L, "test deck", testUserProfile,
                LanguageCode.ENGLISH, LanguageCode.DANISH, new ArrayList<>());
        testUserProfile.getDecks().add(testDeck);
        testDeck.getFlashcards().add(testCard);

        given(mockedFlashcardService.getCardById(testCardId)).willReturn(testCard);
        try (MockedStatic<PrincipalRetriever> mockedStaticPR = Mockito.mockStatic(PrincipalRetriever.class)) {
            mockedStaticPR.when(PrincipalRetriever::retrieveAccount).thenReturn(testAccount);

        // when
            underTest.applyRating(testCardId, ratingTypeParam);
        }

        // then
        ArgumentCaptor<Long> argCaptorForLong = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Flashcard> argCaptorForFlashcard = ArgumentCaptor.forClass(Flashcard.class);
        ArgumentCaptor<RatingType> argCaptorForRatingType = ArgumentCaptor.forClass(RatingType.class);
        InOrder inOrder = Mockito.inOrder(mockedFlashcardService, mockedSpacedRepetitionAlgorithm);

        then(mockedFlashcardService).should(inOrder).getCardById(argCaptorForLong.capture());
        then(mockedSpacedRepetitionAlgorithm).should(inOrder).apply(argCaptorForFlashcard.capture(),
                argCaptorForRatingType.capture());
        assertEquals(testCardId, argCaptorForLong.getValue());
        assertEquals(testCardId, argCaptorForFlashcard.getValue().getId());
        assertEquals(ratingTypeParam, argCaptorForRatingType.getValue());
    }
}