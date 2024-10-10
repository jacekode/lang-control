package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
import dev.jlynx.langcontrol.deck.dto.DeckOverview;
import dev.jlynx.langcontrol.deck.view.DeckView;
import dev.jlynx.langcontrol.exception.AccessForbiddenException;
import dev.jlynx.langcontrol.exception.DeckCreationException;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.flashcard.WordFlashcardService;
import dev.jlynx.langcontrol.lang.LanguageCode;
import dev.jlynx.langcontrol.role.DefinedRoleValue;
import dev.jlynx.langcontrol.role.Role;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.userprofile.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class DeckServiceImplTest {

    private DeckServiceImpl underTest;

    @Mock
    private DeckRepository mockedDeckRepository;

    @Mock
    private UserProfileService mockedUserProfileService;

    @Mock
    private WordFlashcardService mockedWordFlashcardService;

    @Captor
    private ArgumentCaptor<Deck> deckCaptor;


    @BeforeEach
    void setUp() {
        this.underTest = new DeckServiceImpl(mockedDeckRepository, mockedUserProfileService, mockedWordFlashcardService);
    }

    @Test
    void createNewDeck_ShouldThrow_WhenTargetAndSourceLanguageAreTheSame() {
        // given
        LanguageCode langCode = LanguageCode.ENGLISH;
        CreateDeckRequest sameLangBody = new CreateDeckRequest("Test deck", langCode, langCode);

        // then
        assertThrows(DeckCreationException.class, () -> underTest.createNewDeck(sameLangBody));
        then(mockedDeckRepository).shouldHaveNoInteractions();
    }

    @Test
    void createNewDeck_ShouldAddNewDeckToUserProfile_WhenRequestBodyIsCorrect() {
        // given
        CreateDeckRequest reqBody = new CreateDeckRequest("Test deck", LanguageCode.ENGLISH, LanguageCode.GERMAN);
        long deckId = 14L;
//        Account testAccount = new Account(
//                7L,
//                "test@example.com",
//                TEST_PWD,
//                List.of(new Role(1L, DefinedRoleValue.USER)),
//                true, true,
//                true, true
//        );
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setDecks(new ArrayList<>());
//        testUserProfile.setAccount(testAccount);
//        testAccount.setUserProfile(testUserProfile);
        given(mockedUserProfileService.retrieveCurrentProfileEntity()).willReturn(testUserProfile);
        given(mockedDeckRepository.save(any(Deck.class))).willAnswer(new Answer<Deck>() {
            @Override
            public Deck answer(InvocationOnMock invocation) throws Throwable {
                Deck deck = invocation.getArgument(0);
                deck.setId(deckId);
                return deck;
            }
        });

        // when
        DeckOverview returned = underTest.createNewDeck(reqBody);

        // then
        assertEquals(1, testUserProfile.getDecks().size());
        Deck createdDeck = testUserProfile.getDecks().get(0);
        assertNotNull(createdDeck.getId());
        assertEquals(reqBody.name(), createdDeck.getName());
        assertEquals(reqBody.targetLang(), createdDeck.getTargetLang());
        assertEquals(reqBody.sourceLang(), createdDeck.getSourceLang());
        then(mockedDeckRepository).should(times(1)).save(deckCaptor.capture());
        Deck repoArg = deckCaptor.getValue();
        assertEquals(reqBody.name(), repoArg.getName());
        assertEquals(reqBody.targetLang(), repoArg.getTargetLang());
        assertEquals(reqBody.sourceLang(), repoArg.getSourceLang());
        assertTrue(returned.id() > 0);
        assertEquals(reqBody.name(), returned.name());
        assertEquals(reqBody.targetLang(), returned.targetLang());
        assertEquals(reqBody.sourceLang(), returned.sourceLang());
    }

    @Test
    void getAllCurrentUserProfileDecks_ShouldReturnAllCurrentUserProfileDecks() {
        // given
        Deck deck1 = new Deck(12L, "Deck One", null,
                LanguageCode.SPANISH, LanguageCode.ENGLISH, new ArrayList<>());
        Deck deck2 = new Deck(15L, "Deck Two", null,
                LanguageCode.SPANISH, LanguageCode.GERMAN, new ArrayList<>());
        List<Deck> deckList = new ArrayList<>(List.of(deck1, deck2));
        List<DeckView> deckViewList = deckList
                .stream()
                .map(d -> new DeckView(d.getId(), d.getName(),
                        d.getTargetLang(), d.getSourceLang()))
                .toList();

//        Account testAccount = new Account(
//                7L, "username",
//                TEST_PWD,
//                List.of(new Role(1L, DefinedRoleValue.USER)),
//                true, true,
//                true, true);
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setDecks(deckList);
        deck1.setUserProfile(testUserProfile);
        deck2.setUserProfile(testUserProfile);
//        testUserProfile.setAccount(testAccount);
//        testAccount.setUserProfile(testUserProfile);
        given(mockedUserProfileService.retrieveCurrentProfileEntity()).willReturn(testUserProfile);
        given(mockedDeckRepository.findByUserProfile(any(UserProfile.class))).willReturn(deckViewList);

        // when
         List<DeckView> returned = underTest.getAllCurrentUserProfileDecks();

        // then
        then(mockedDeckRepository).should(times(1)).findByUserProfile(testUserProfile);
        assertEquals(deckViewList, returned);
    }

    @Test
    void getDeckById_ShouldThrow_WhenDeckNotFound() {
        // given
        long testDeckId = 34L;
        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

        // when + then
        assertThrows(AssetNotFoundException.class, () -> underTest.getDeckById(testDeckId));
    }

    @Test
    void getDeckById_ShouldThrow_WhenRequestedDeckHasDifferentUserProfileThanTheCurrentOne() {
        // given
        long testDeckId = 34L;
        Account testAccount = new Account(
                7L, "username",
                "aBt3%4gh45srSuiAe5h%EA5h",
                List.of(new Role(1L, DefinedRoleValue.USER)),
                true, true,
                true, true);
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setAccount(testAccount);
        testUserProfile.setDecks(new ArrayList<>());
        testAccount.setUserProfile(testUserProfile);
        UserProfile testUserProfile2 = new UserProfile(14L, "Jane Smith");
        testUserProfile2.setDecks(new ArrayList<>());
        Deck testDeck = new Deck(testDeckId, "Deck One", testUserProfile2,
                LanguageCode.SPANISH, LanguageCode.ENGLISH, new ArrayList<>());

        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.of(testDeck));
        given(mockedUserProfileService.retrieveCurrentProfileEntity()).willReturn(testUserProfile);

        // when + then
        assertThrows(AccessForbiddenException.class, () -> underTest.getDeckById(testDeckId));
    }

    @Test
    void getDeckById_ShouldReturnFoundDeck() {
        // given
        long testDeckId = 34L;
        Account testAccount = new Account(
                7L, "username",
                "aBt3%4gh45srSuiAe5h%EA5h",
                List.of(new Role(1L, DefinedRoleValue.USER)),
                true, true,
                true, true);
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setAccount(testAccount);
        testUserProfile.setDecks(new ArrayList<>());
        testAccount.setUserProfile(testUserProfile);
        Deck testDeck = new Deck(testDeckId, "Deck One", testUserProfile,
                LanguageCode.SPANISH, LanguageCode.ENGLISH, new ArrayList<>());

        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.of(testDeck));
        given(mockedUserProfileService.retrieveCurrentProfileEntity()).willReturn(testUserProfile);

        // when
        DeckOverview returned = underTest.getDeckById(testDeckId);

        // then
        assertEquals(testDeck.getId(), returned.id());
        assertEquals(testDeck.getName(), returned.name());
        assertEquals(testDeck.getSourceLang(), returned.sourceLang());
        assertEquals(testDeck.getTargetLang(), returned.targetLang());
    }

    @Test
    void deleteDeck_ShouldDeleteDeckWithGivenId() {
        // given
        long testDeckId = 34L;
        Account testAccount = new Account(
                7L, "username",
                "aBt3%4gh45srSuiAe5h%EA5h",
                List.of(new Role(1L, DefinedRoleValue.USER)),
                true, true,
                true, true);
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setAccount(testAccount);
        testUserProfile.setDecks(new ArrayList<>());
        testAccount.setUserProfile(testUserProfile);
        Deck testDeck = new Deck(testDeckId, "Deck One", testUserProfile,
                LanguageCode.SPANISH, LanguageCode.ENGLISH, new ArrayList<>());

        given(mockedDeckRepository.findById(testDeckId)).willReturn(Optional.of(testDeck));
        given(mockedUserProfileService.retrieveCurrentProfileEntity()).willReturn(testUserProfile);

        // when
        underTest.deleteDeck(testDeckId);

        // then
        then(mockedDeckRepository).should(times(1)).delete(testDeck);
    }

    @Test
    void deleteDeck_ShouldThrow_WhenDeckNotFound() {
        // given
        long testDeckId = 34L;
        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

        // when + then
        assertThrows(AssetNotFoundException.class, () -> underTest.deleteDeck(testDeckId));
    }

    @Test
    void deleteDeck_ShouldThrow_WhenRequestedDeckHasDifferentUserProfileThanTheCurrentOne() {
        // given
        long testDeckId = 34L;
        Account testAccount = new Account(
                7L, "username",
                "aBt3%4gh45srSuiAe5h%EA5h",
                List.of(new Role(1L, DefinedRoleValue.USER)),
                true, true,
                true, true);
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setAccount(testAccount);
        testUserProfile.setDecks(new ArrayList<>());
        testAccount.setUserProfile(testUserProfile);
        UserProfile testUserProfile2 = new UserProfile(14L, "Jane Smith");
        testUserProfile2.setDecks(new ArrayList<>());
        Deck testDeck = new Deck(testDeckId, "Deck One", testUserProfile2,
                LanguageCode.SPANISH, LanguageCode.ENGLISH, new ArrayList<>());

        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.of(testDeck));
        given(mockedUserProfileService.retrieveCurrentProfileEntity()).willReturn(testUserProfile);

        // when + then
        assertThrows(AccessForbiddenException.class, () -> underTest.deleteDeck(testDeckId));
    }
}