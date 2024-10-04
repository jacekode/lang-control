package dev.jlynx.langcontrol.deck;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
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
import dev.jlynx.langcontrol.util.AuthRetriever;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class DeckServiceImplTest {

    public static final String TEST_PWD = "aBt3%4gh45srSuiAe5h%EA5h";

    private DeckServiceImpl underTest;

    @Mock
    private DeckRepository mockedDeckRepository;

    @Mock
    private UserProfileService mockedUserProfileService;

    @Mock
    private WordFlashcardService mockedWordFlashcardService;

    @BeforeEach
    void setUp() {
        // todo: use @InjectMocks instead of the initialisation below
        this.underTest = new DeckServiceImpl(mockedDeckRepository, mockedUserProfileService, mockedWordFlashcardService);
    }

    @Test
    void createNewDeck_ShouldThrowException_WhenTargetAndSourceLanguageAreTheSame() {
        // given
        LanguageCode langCode = LanguageCode.ENGLISH;
        CreateDeckRequest sameLangsBody = new CreateDeckRequest("Invalid deck", langCode, langCode);

        // then
        assertThrows(DeckCreationException.class, () -> underTest.createNewDeck(sameLangsBody));
    }

    @Test
    void createNewDeck_ShouldAddNewDeckToUserProfile_WhenDataIsCorrect() {
        // given
        CreateDeckRequest body = new CreateDeckRequest("Test deck", LanguageCode.ENGLISH, LanguageCode.GERMAN);
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

        // when
        underTest.createNewDeck(body);

        // then
        assertEquals(1, testUserProfile.getDecks().size());
        Deck createdDeck = testUserProfile.getDecks().get(0);
        assertNotNull(createdDeck.getId());
        assertEquals("Test deck", createdDeck.getName());
        assertEquals(LanguageCode.ENGLISH, createdDeck.getTargetLang());
        assertEquals(LanguageCode.GERMAN, createdDeck.getSourceLang());
    }

    @Test
    void getAllDecks_ShouldReturnAllCurrentUserProfileDecksOfTheCurrentUser() {
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

        Account testAccount = new Account(
                7L, "username",
                TEST_PWD,
                List.of(new Role(1L, DefinedRoleValue.USER)),
                true, true,
                true, true);
        UserProfile testUserProfile = new UserProfile(7L, "John Doe");
        testUserProfile.setAccount(testAccount);
        testUserProfile.setDecks(deckList);
        testAccount.setUserProfile(testUserProfile);
        deck1.setUserProfile(testUserProfile);
        deck2.setUserProfile(testUserProfile);

        given(mockedUserProfileService.retrieveCurrentProfileEntity()).willReturn(testUserProfile);
        given(mockedDeckRepository.findByUserProfile(testUserProfile)).willReturn(deckViewList);

        // when
            List<DeckView> result = underTest.getAllCurrentUserProfileDecks();

        // then
            assertEquals(deckViewList, result);
    }

    @Test
    void getDeckById_ShouldThrowException_WhenDeckIsNotFound() {
        // given
        long testDeckId = 34L;
        given(mockedDeckRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());
        try (MockedStatic<AuthRetriever> mockedStaticPR = Mockito.mockStatic(AuthRetriever.class)) {

        // when + then
           assertThrows(AssetNotFoundException.class, () -> underTest.getDeckById(testDeckId));
        }
    }

    @Test
    void getDeckById_ShouldThrowException_WhenFoundDeckHasDifferentUserProfileThanTheCurrentOne() {
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
        try (MockedStatic<AuthRetriever> mockedStaticPR = Mockito.mockStatic(AuthRetriever.class)) {
            mockedStaticPR.when(AuthRetriever::retrieveCurrentAccount).thenReturn(testAccount);

        // when + then
            assertThrows(AccessForbiddenException.class, () -> underTest.getDeckById(testDeckId));
        }
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
        try (MockedStatic<AuthRetriever> mockedStaticPR = Mockito.mockStatic(AuthRetriever.class)) {
            mockedStaticPR.when(AuthRetriever::retrieveCurrentAccount).thenReturn(testAccount);

        // when
            Deck result = underTest.getDeckById(testDeckId);

        // then
            assertEquals(testDeck.getId(), result.getId());
            assertEquals(testDeck.getName(), result.getName());
            assertEquals(testDeck.getSourceLang(), result.getSourceLang());
            assertEquals(testDeck.getTargetLang(), result.getTargetLang());
        }
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
        try (MockedStatic<AuthRetriever> mockedStaticPR = Mockito.mockStatic(AuthRetriever.class)) {
            mockedStaticPR.when(AuthRetriever::retrieveCurrentAccount).thenReturn(testAccount);

            // when
            underTest.deleteDeck(testDeckId);

            // then
            then(mockedDeckRepository).should().delete(testDeck);
        }
    }
}