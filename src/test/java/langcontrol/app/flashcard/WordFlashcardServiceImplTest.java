package langcontrol.app.flashcard;

import langcontrol.app.account.Account;
import langcontrol.app.generator.Dictionary;
import langcontrol.app.security.DefinedRoleValue;
import langcontrol.app.security.Role;
import langcontrol.app.userprofile.UserProfile;
import langcontrol.app.deck.Deck;
import langcontrol.app.deck.DeckRepository;
import langcontrol.app.exception.GeneralNotFoundException;
import langcontrol.app.deck.LanguageCode;
import langcontrol.app.util.PrincipalRetriever;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class WordFlashcardServiceImplTest {

    private WordFlashcardRepository mockedWordFlashcardRepository;
    private DeckRepository mockedDeckRepository;
    private Dictionary mockedDictionary;
    private WordFlashcardServiceImpl underTest;

    @BeforeEach
    void setUp() {
        mockedWordFlashcardRepository = Mockito.mock(WordFlashcardRepository.class);
        mockedDeckRepository = Mockito.mock(DeckRepository.class);
        mockedDictionary = Mockito.mock(Dictionary.class);
        underTest = new WordFlashcardServiceImpl(mockedWordFlashcardRepository, , mockedDeckRepository, mockedDictionary);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 0, -1})
    void deleteFlashcard_ShouldDeleteTheFlashcardWithSpecifiedId(long id) {
        // given
        Account testAccount = new Account(56L, "username", "password",
                List.of(new Role(1L, DefinedRoleValue.USER)));
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setAccount(testAccount);
        testUserProfile.setDecks(new ArrayList<>());
        testAccount.setUserProfile(testUserProfile);
        Deck testDeck = new Deck(78L, "test deck", testUserProfile,
                LanguageCode.ENGLISH, LanguageCode.ITALIAN, new ArrayList<>());

        testUserProfile.setDecks(new ArrayList<>());
        testUserProfile.getDecks().add(testDeck);

        WordFlashcard testCard = new WordFlashcard();
        testCard.setId(id);
        testCard.setDeck(testDeck);
        testCard.setTranslatedWord("front");
        testCard.setTargetWord("back");
        testDeck.getFlashcards().add(testCard);

        given(mockedWordFlashcardRepository.findById(Mockito.anyLong())).willReturn(Optional.of(testCard));
        try (MockedStatic<PrincipalRetriever> mockedStaticPR = Mockito.mockStatic(PrincipalRetriever.class)) {
            mockedStaticPR.when(PrincipalRetriever::retrieveAccount).thenReturn(testAccount);

        // when
            underTest.deleteFlashcard(id);
        }

        // then
        ArgumentCaptor<WordFlashcard> argumentCaptor = ArgumentCaptor.forClass(WordFlashcard.class);
        verify(mockedWordFlashcardRepository).delete(argumentCaptor.capture());
        assertEquals(id, argumentCaptor.getValue().getId());

    }

    @Test
    void createNewFlashcard_ShouldCreateFlashcard_WhenDeckIsFound() {
        // given
        long deckId = 2L;
        FlashcardCreationDTO creationDto = new FlashcardCreationDTO();
        creationDto.setFront("test front");
        creationDto.setBack("test back");
        creationDto.setPartOfSpeech(PartOfSpeech.PHRASE);
        creationDto.setDynamicExamples(true);
        Deck mockedDeck = Mockito.mock(Deck.class);

        Account testAccount = new Account(56L, "username", "password",
                List.of(new Role(1L, DefinedRoleValue.USER)));
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setAccount(testAccount);
        testUserProfile.setDecks(new ArrayList<>());
        testAccount.setUserProfile(testUserProfile);
        Deck testDeck = new Deck(deckId, "test deck", testUserProfile,
                LanguageCode.ENGLISH, LanguageCode.ITALIAN, new ArrayList<>());
        testUserProfile.setDecks(new ArrayList<>());
        testUserProfile.getDecks().add(testDeck);

        when(mockedDeckRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testDeck));
        try (MockedStatic<PrincipalRetriever> mockedStaticPR = Mockito.mockStatic(PrincipalRetriever.class)) {
            mockedStaticPR.when(PrincipalRetriever::retrieveAccount).thenReturn(testAccount);

        // when
            underTest.createNewFlashcard(deckId, creationDto);
        }

        // then
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(mockedDeckRepository).findById(longArgumentCaptor.capture());
        assertEquals(deckId, longArgumentCaptor.getValue());
        WordFlashcard addedCard = testDeck.getFlashcards().get(0);
        assertEquals("test front", addedCard.getTranslatedWord());
        assertEquals("test back", addedCard.getTargetWord());
        assertEquals(PartOfSpeech.PHRASE, addedCard.getPos());
        assertTrue(addedCard.isDynamicExamples());
    }

    @Test
    void createNewFlashcard_ShouldThrowException_WhenDeckIsNotFound() {
        // given
        long deckId = 2L;

        Account testAccount = new Account(56L, "username", "password",
                List.of(new Role(1L, DefinedRoleValue.USER)));

        FlashcardCreationDTO creationDto = new FlashcardCreationDTO();
        creationDto.setFront("test front");
        creationDto.setBack("test back");
        creationDto.setPartOfSpeech(PartOfSpeech.PHRASE);
        creationDto.setDynamicExamples(false);

        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());
        try (MockedStatic<PrincipalRetriever> mockedStaticPR = Mockito.mockStatic(PrincipalRetriever.class)) {
            mockedStaticPR.when(PrincipalRetriever::retrieveAccount).thenReturn(testAccount);

        // when, then
        assertThrows(GeneralNotFoundException.class,
                () -> underTest.createNewFlashcard(deckId, creationDto));
        }

    }

    @Test
    void fetchShuffledFlashcardsReadyForReviewWithLimit_ShouldReturnFlashcards() {
        // given
        String zoneId = "America/Los_Angeles";
        long deckId = 2;
        int limit = 15;
        LocalDateTime locDateTimeInUtcExpected = LocalDateTime.now(Clock.systemUTC());
        LocalDate locDateExpected = ZonedDateTime.now(ZoneId.of(zoneId)).toLocalDateTime().toLocalDate();

        Account testAccount = new Account(56L, "username", "password",
                List.of(new Role(1L, DefinedRoleValue.USER)));
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setAccount(testAccount);
        testUserProfile.setDecks(new ArrayList<>());
        testAccount.setUserProfile(testUserProfile);
        Deck testDeck = new Deck(deckId, "test deck", testUserProfile,
                LanguageCode.ENGLISH, LanguageCode.ITALIAN, new ArrayList<>());
        testUserProfile.setDecks(new ArrayList<>());
        testUserProfile.getDecks().add(testDeck);

        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.of(testDeck));
        try (MockedStatic<PrincipalRetriever> mockedStaticPR = Mockito.mockStatic(PrincipalRetriever.class)) {
            mockedStaticPR.when(PrincipalRetriever::retrieveAccount).thenReturn(testAccount);

        // when
            Deque<WordFlashcard> result = underTest.fetchShuffleReadyForView(deckId, zoneId, limit);
        }

        // then
        InOrder inOrder = Mockito.inOrder(mockedDeckRepository, mockedWordFlashcardRepository);
        ArgumentCaptor<LocalDateTime> locDateTimeArgCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDate> locDateArgCaptor = ArgumentCaptor.forClass(LocalDate.class);

        then(mockedDeckRepository).should(inOrder).findById(deckId);
        then(mockedWordFlashcardRepository).should(inOrder).findReadyForReviewFlashcardsByDeck(
                eq(testDeck),
                locDateTimeArgCaptor.capture(),
                locDateArgCaptor.capture(),
                eq(limit));

        LocalDateTime locDateTimeInUtcArg = locDateTimeArgCaptor.getValue();
        LocalDate locDateArg = locDateArgCaptor.getValue();

        assertAll("LocalDateTime in UTC assertions",
                () -> assertEquals(locDateTimeInUtcExpected.getYear(), locDateTimeInUtcArg.getYear()),
                () -> assertEquals(locDateTimeInUtcExpected.getMonth(), locDateTimeInUtcArg.getMonth()),
                () -> assertEquals(locDateTimeInUtcExpected.getDayOfMonth(), locDateTimeInUtcArg.getDayOfMonth()),
                () -> assertEquals(locDateTimeInUtcExpected.getHour(), locDateTimeInUtcArg.getHour()),
                () -> assertEquals(locDateTimeInUtcExpected.getMinute(), locDateTimeInUtcArg.getMinute()),
                () -> assertEquals(locDateTimeInUtcExpected.getSecond(), locDateTimeInUtcArg.getSecond())
        );
        assertAll("LocalDate assertions",
                () -> assertEquals(locDateExpected.getYear(), locDateArg.getYear()),
                () -> assertEquals(locDateExpected.getMonth(), locDateArg.getMonth()),
                () -> assertEquals(locDateExpected.getDayOfMonth(), locDateArg.getDayOfMonth())
        );
    }

    @Test
    void fetchShuffledFlashcardsReadyForReviewWithLimit_ShouldThrowException_WhenDeckNotFound() {
        // given
        String zoneId = "America/Los_Angeles";
        long deckId = 2;
        int limit = 15;
        Account testAccount = new Account(56L, "username", "password",
                List.of(new Role(1L, DefinedRoleValue.USER)));

        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

        try (MockedStatic<PrincipalRetriever> mockedStaticPR = Mockito.mockStatic(PrincipalRetriever.class)) {
            mockedStaticPR.when(PrincipalRetriever::retrieveAccount).thenReturn(testAccount);

        // when, then
            assertThrows(GeneralNotFoundException.class,
                    () -> underTest.fetchShuffleReadyForView(deckId, zoneId, limit));
        }
    }

    @Test
    void fetchShuffledFlashcardsReadyForReviewWithLimit_ShouldThrowException_WhenLimitIsLessThanZero() {
        // given
        String zoneId = "America/Los_Angeles";
        long deckId = 2;
        int limit = -20;

        // then
        assertThrows(IllegalArgumentException.class,
                () -> underTest.fetchShuffleReadyForView(deckId, zoneId, limit));
    }

    @Test
    void fetchShuffledFlashcardsReadyForReviewWithLimit_ShouldThrowException_WhenZoneIdIsNotCorrect() {
        // given
        String zoneId = "American/Los_Angeles";
        long deckId = 2;
        int limit = 15;

        // then
        assertThrows(IllegalArgumentException.class,
                () -> underTest.fetchShuffleReadyForView(deckId, zoneId, limit));
    }

    @Test
    void getAllFlashcardsByDeck_ShouldGetFlashcards() {
        // given
        Account testAccount = new Account(56L, "username", "password",
                List.of(new Role(1L, DefinedRoleValue.USER)));
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setAccount(testAccount);
        testUserProfile.setDecks(new ArrayList<>());
        testAccount.setUserProfile(testUserProfile);
        Deck testDeck = new Deck(2L, "test deck", testUserProfile,
                LanguageCode.ENGLISH, LanguageCode.ITALIAN, new ArrayList<>());
        testUserProfile.setDecks(new ArrayList<>());
        testUserProfile.getDecks().add(testDeck);

        try (MockedStatic<PrincipalRetriever> mockedStaticPR = Mockito.mockStatic(PrincipalRetriever.class)) {
            mockedStaticPR.when(PrincipalRetriever::retrieveAccount).thenReturn(testAccount);

        // when
            underTest.getAllFlashcardsByDeck(testDeck);
        }

        // then
        ArgumentCaptor<Deck> deckArgumentCaptor = ArgumentCaptor.forClass(Deck.class);
        verify(mockedWordFlashcardRepository).findByDeck(deckArgumentCaptor.capture());
        assertEquals(testDeck, deckArgumentCaptor.getValue());
    }
}
