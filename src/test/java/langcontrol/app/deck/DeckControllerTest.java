package langcontrol.app.deck;

import langcontrol.app.flashcard.Flashcard;
import langcontrol.app.flashcard.FlashcardService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeckController.class)
class DeckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeckService mockedDeckService;

    @MockBean
    private FlashcardService mockedFlashcardService;

    @Captor
    private ArgumentCaptor<Deck> deckArgCaptor;


    @WithMockUser(username = "username")
    @Test
    void getCreateDeckPage_ShouldReturnAddDeckPage() throws Exception {
        mockMvc.perform(get("/add-deck"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-deck"))
                .andExpect(model().attribute("deckToCreate", instanceOf(Deck.class)))
                .andExpect(model().attribute("languageCodes", LanguageCode.values()));
    }

    @WithAnonymousUser
    @Test
    void getCreateDeckPage_ShouldReturnUnauthorizedStatusCode_WhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/create-deck"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "username")
    @Test
    void createDeck_ShouldCreateNewDeckAndRedirectToDeckCreationPage() throws Exception {
        // given
        CreateDeckDTO deckDto = new CreateDeckDTO("Test Deck",
                LanguageCode.SPANISH, LanguageCode.ENGLISH);
        Mockito.doNothing().when(mockedDeckService).createNewDeck(Mockito.any(Deck.class));

        // when
        mockMvc.perform(post("/add-deck")
                        .with(csrf())
                .flashAttr("deckToCreate", deckDto))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/add-deck"));

        // then
        then(mockedDeckService).should(times(1)).createNewDeck(deckArgCaptor.capture());
        Deck argument = deckArgCaptor.getValue();
        assertEquals(deckDto.getName(), argument.getName());
        assertEquals(deckDto.getSourceLanguage(), argument.getSourceLanguage());
        assertEquals(deckDto.getTargetLanguage(), argument.getTargetLanguage());
    }

    @WithAnonymousUser
    @Test
    void createDeck_ShouldReturnUnauthorizedStatusCode_WhenUserIsNotAuthenticated() throws Exception {
        // given
        CreateDeckDTO deckDto = new CreateDeckDTO("Test Deck",
                LanguageCode.SPANISH, LanguageCode.ENGLISH);
        Mockito.doNothing().when(mockedDeckService).createNewDeck(Mockito.any(Deck.class));

        // when + then
        mockMvc.perform(post("/create-deck")
                        .with(csrf())
                        .flashAttr("deckToCreate", deckDto))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "username")
    @Test
    void getShowDecksPage_ShouldReturnPageWithAllDecks() throws Exception {
        // given
        DeckView deckView1 = new DeckView(67L, "Deck One", LanguageCode.SPANISH, LanguageCode.ENGLISH);
        DeckView deckView2 = new DeckView(72L, "Deck Two", LanguageCode.SPANISH, LanguageCode.GERMAN);
        List<DeckView> deckViewList = List.of(deckView1, deckView2);
        given(mockedDeckService.getAllDecks()).willReturn(deckViewList);

        // when + then
        mockMvc.perform(get("/decks"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("decks", deckViewList))
                .andExpect(view().name("all-decks"));

        then(mockedDeckService).should().getAllDecks();
    }

    @WithAnonymousUser
    @Test
    void getShowDecksPage_ShouldReturnUnauthorizedStatusCode_WhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/decks"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "username")
    @Test
    void deleteDeck_ShouldDeleteDeckAndRedirectToAllDecksPage() throws Exception {
        // given
        long testDeckId = 145L;

        // when
        mockMvc.perform(post(String.format("/deck/%d/delete", testDeckId))
                        .with(csrf()))

                // then
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/decks"));
        then(mockedDeckService).should(times(1)).deleteDeck(testDeckId);
    }

    @WithMockUser(username = "username")
    @Test
    void deleteDeck_ShouldReturnBadRequestStatusCode_WhenDeckIdIsLessThanOne() throws Exception {
        // given
        long testDeckId = 0L;

        // when
        mockMvc.perform(post(String.format("/deck/%d/delete", testDeckId))
                        .with(csrf()))
                // then
                .andExpect(status().isBadRequest());
    }

    @WithAnonymousUser
    @Test
    void deleteDeck_ShouldReturnUnauthorizedStatusCode_WhenUserIsNotAuthenticated() throws Exception {
        // given
        long testDeckId = 145L;

        // when
        mockMvc.perform(post(String.format("/deck/%d/delete", testDeckId))
                        .with(csrf()))
                // then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "username")
    @Test
    void showAllDecksFlashcards_ShouldReturnAllDeckCardsPage() throws Exception {
        // given
        long testDeckId = 287L;
        Flashcard card1 = Flashcard.inInitialReviewModeState()
                .front("front1").back("back1")
                .targetLanguage(LanguageCode.GERMAN)
                .sourceLanguage(LanguageCode.ENGLISH)
                .build();
        Flashcard card2 = Flashcard.inInitialReviewModeState()
                .front("front2").back("back2")
                .targetLanguage(LanguageCode.GERMAN)
                .sourceLanguage(LanguageCode.SPANISH)
                .build();
        List<Flashcard> testCardList = List.of(card1, card2);
        Deck testDeck = new Deck(testDeckId, "Test Deck", null,
                LanguageCode.GERMAN, LanguageCode.ENGLISH, testCardList);
        given(mockedDeckService.getDeckById(testDeckId)).willReturn(testDeck);
        given(mockedFlashcardService.getAllFlashcardsByDeck(testDeck)).willReturn(testCardList);

        // when
        mockMvc.perform(get(String.format("/deck/%s/cards", testDeckId)))

                // then
                .andExpect(status().isOk())
                .andExpect(model().attribute("deck", testDeck))
                .andExpect(model().attribute("deckFlashcards", testCardList));
        then(mockedFlashcardService).should().getAllFlashcardsByDeck(testDeck);
    }

    @WithMockUser(username = "username")
    @Test
    void showAllDecksFlashcards_ShouldReturnBadRequestStatusCode_WhenDeckIdIsLessThanOne() throws Exception {
        // given
        long testDeckId = 0L;

        // when
        mockMvc.perform(get(String.format("/deck/%s/cards", testDeckId)))
                // then
                .andExpect(status().isBadRequest());
    }

    @WithAnonymousUser
    @Test
    void showAllDecksFlashcards_ShouldReturnUnauthorizedStatusCode_WhenUserIsNotAuthenticated() throws Exception {
        // given
        long testDeckId = 287L;

        // when
        mockMvc.perform(get(String.format("/deck/%s/cards", testDeckId)))
                // then
                .andExpect(status().isUnauthorized());
    }
}