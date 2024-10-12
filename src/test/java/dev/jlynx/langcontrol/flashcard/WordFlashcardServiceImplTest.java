package dev.jlynx.langcontrol.flashcard;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.flashcard.dto.CreateWordFlashcardRequest;
import dev.jlynx.langcontrol.generator.Dictionary;
import dev.jlynx.langcontrol.role.DefinedRoleValue;
import dev.jlynx.langcontrol.role.Role;
import dev.jlynx.langcontrol.spacedrepetition.SpacedRepetitionAlgorithm;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.deck.Deck;
import dev.jlynx.langcontrol.deck.DeckRepository;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.userprofile.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WordFlashcardServiceImplTest {

    @InjectMocks
    private WordFlashcardServiceImpl underTest;

    @Mock
    private WordFlashcardRepository mockedWordFlashcardRepository;
    @Mock
    private DeckRepository mockedDeckRepository;
    @Mock
    private Dictionary mockedDictionary;
    @Mock
    private SpacedRepetitionAlgorithm mockedSpacedRepetitionAlgorithm;
    @Mock
    private UserProfileService mockedUserProfileService;

    @Captor
    private ArgumentCaptor<WordFlashcard> wordFlashcardCaptor;


    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 17})
    void deleteFlashcard_ShouldDeleteTheFlashcardWithSpecifiedId(long id) {
        // given
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setDecks(new ArrayList<>());
        Deck testDeck = new Deck(78L, "test deck", testUserProfile,
                LanguageCode.ENGLISH, LanguageCode.ITALIAN, new ArrayList<>());
        testUserProfile.setDecks(new ArrayList<>());
        testUserProfile.getDecks().add(testDeck);
        WordFlashcard testCard = WordFlashcard.inInitialLearnMode()
                .withDeck(testDeck)
                .withTranslatedWord("translation")
                .withTargetWord("target")
                .build();
        testCard.setId(id);
        testDeck.getFlashcards().add(testCard);
        given(mockedWordFlashcardRepository.findById(Mockito.anyLong())).willReturn(Optional.of(testCard));
        given(mockedUserProfileService.retrieveCurrentProfileEntity()).willReturn(testUserProfile);

        // when
        underTest.deleteFlashcard(id);

        // then
        verify(mockedWordFlashcardRepository, times(1)).delete(wordFlashcardCaptor.capture());
        assertEquals(id, wordFlashcardCaptor.getValue().getId());
    }

    @Test
    void createNewFlashcard_ShouldCreateFlashcard() {
        // given
        long deckId = 2L;
        long cardId = 16L;
        CreateWordFlashcardRequest reqBody = new CreateWordFlashcardRequest(
                "translation",
                "target",
                PartOfSpeech.NOUN,
                false,
                "Translated example.",
                "Target example,",
                deckId
        );
//        Account testAccount = new Account(56L, "username", "password",
//                List.of(new Role(1L, DefinedRoleValue.USER)));
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setDecks(new ArrayList<>());
//        testUserProfile.setAccount(testAccount);
//        testAccount.setUserProfile(testUserProfile);
        Deck testDeck = new Deck(deckId, "test deck", testUserProfile,
                LanguageCode.ENGLISH, LanguageCode.ITALIAN, new ArrayList<>());
        testUserProfile.setDecks(new ArrayList<>());
        testUserProfile.getDecks().add(testDeck);
        when(mockedDeckRepository.findById(deckId)).thenReturn(Optional.of(testDeck));
        when(mockedUserProfileService.retrieveCurrentProfileEntity()).thenReturn(testUserProfile);
        when(mockedWordFlashcardRepository.save(any(WordFlashcard.class))).thenAnswer(new Answer<WordFlashcard>() {
            @Override
            public WordFlashcard answer(InvocationOnMock invocation) throws Throwable {
                WordFlashcard flashcard = invocation.getArgument(0);
                flashcard.setId(cardId);
                return flashcard;
            }
        });

        // when
        underTest.createNewFlashcard(reqBody);

        // then
        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        verify(mockedDeckRepository, times(1)).findById(longCaptor.capture());
        assertEquals(deckId, longCaptor.getValue());
        assertEquals(1, testDeck.getFlashcards().size());

        verify(mockedWordFlashcardRepository, times(1)).save(wordFlashcardCaptor.capture());
        WordFlashcard arg = wordFlashcardCaptor.getValue();
        assertEquals(reqBody.translatedWord(), arg.getTranslatedWord());
        assertEquals(reqBody.targetWord(), arg.getTargetWord());
        assertEquals(reqBody.partOfSpeech(), arg.getPartOfSpeech());
        assertEquals(reqBody.dynamicExamples(), arg.isDynamicExamples());
        assertEquals(reqBody.deckId(), arg.getDeck().getId());
    }

    @Test
    void createNewFlashcard_ShouldThrow_WhenDeckIsNotFound() {
        // given
        long deckId = 2L;
        Account testAccount = new Account(56L, "username", "password",
                List.of(new Role(1L, DefinedRoleValue.USER)));
        CreateWordFlashcardRequest reqBody = new CreateWordFlashcardRequest(
                "translation",
                "target",
                PartOfSpeech.NOUN,
                false,
                "Translated example.",
                "Target example,",
                deckId
        );
        given(mockedDeckRepository.findById(deckId)).willReturn(Optional.empty());

        // when, then
        assertThrows(AssetNotFoundException.class, () -> underTest.createNewFlashcard(reqBody));
    }
}
