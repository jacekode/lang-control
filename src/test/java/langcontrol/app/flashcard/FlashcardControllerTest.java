package langcontrol.app.flashcard;

import langcontrol.app.deck.DeckService;
import langcontrol.app.deck.Deck;
import langcontrol.app.deck.LanguageCode;
import langcontrol.app.user_settings.UserSettings;
import langcontrol.app.user_settings.UserSettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlashcardController.class)
class FlashcardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeckService mockedDeckService;

    @MockBean
    private FlashcardService mockedFlashcardService;

    @MockBean
    private UserSettingsService mockedUserSettingsService;


    @WithMockUser(username = "username")
    @Test
    void getAddCardToDeckPage_ShouldReturnAddCardPage() throws Exception {
        // given
        long testDeckId = 94L;
        Deck testDeck = new Deck(testDeckId, "Test Deck", null, LanguageCode.ENGLISH,
                LanguageCode.GERMAN, new ArrayList<>());
        UserSettings testUserSettings = new UserSettings(16L, true, false);
        given(mockedDeckService.getDeckById(testDeckId)).willReturn(testDeck);
        given(mockedUserSettingsService.retrieveCurrentUserSettings()).willReturn(testUserSettings);

        // when
        mockMvc.perform(get("/add-card?deckid=" + testDeckId))

                // then
                .andExpect(status().isOk())
                .andExpect(model().attribute("deck", testDeck))
                .andExpect(model().attribute("newFlashcard", instanceOf(FlashcardCreationDTO.class)))
                .andExpect(view().name("add-card"));

        then(mockedDeckService).should().getDeckById(testDeckId);
    }

    @WithMockUser(username = "username")
    @Test
    void getAddCardToDeckPage_ShouldReturnBadRequestStatusCode_WhenDeckIdIsLessThanOne() throws Exception {
        // given
        long testDeckId = 0L;

        // when + then
        mockMvc.perform(get("/add-card?deckid=" + testDeckId))
                .andExpect(status().isBadRequest());
    }

    @WithAnonymousUser
    @Test
    void getAddCardToDeckPage_ShouldReturnUnauthorizedStatusCode_WhenUserIsNotAuthenticated() throws Exception {
        // given
        long testDeckId = 0L;

        // when + then
        mockMvc.perform(get("/create-card?deckid=" + testDeckId))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "username")
    @Test
    void createNewFlashcard_ShouldCreateNewFlashcardAndRedirectToAddCardPage() throws Exception {
        // given
        long testDeckId = 94L;
        FlashcardCreationDTO testDto = new FlashcardCreationDTO();
        testDto.setFront("front1");
        testDto.setBack("back1");
        testDto.setPartOfSpeech(PartOfSpeech.NOUN);
        testDto.setDynamicExamples(false);

        // when
        mockMvc.perform(post("/add-card?deckid=" + testDeckId)
                        .with(csrf())
                        .flashAttr("newFlashcard", testDto))

                // then
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/add-card?deckid=" + testDeckId));

        then(mockedFlashcardService).should().createNewFlashcard(testDeckId, testDto);
    }

    @WithMockUser(username = "username")
    @Test
    void createNewFlashcard_ShouldReturnBadRequestStatusCode_WhenDeckIdIsLessThanOne() throws Exception {
        // given
        long testDeckId = -1L;
        FlashcardCreationDTO testDto = new FlashcardCreationDTO();
        testDto.setFront("front1");
        testDto.setBack("back1");
        testDto.setPartOfSpeech(PartOfSpeech.NOUN);
        testDto.setDynamicExamples(false);

        // when
        mockMvc.perform(post("/add-card?deckid=" + testDeckId)
                        .with(csrf())
                        .flashAttr("newFlashcard", testDto))
                // then
                .andExpect(status().isBadRequest());
    }

    @WithAnonymousUser
    @Test
    void createNewFlashcard_ShouldReturnUnauthorizedStatusCode_WhenUserIsNotAuthenticated() throws Exception {
        // given
        long testDeckId = 94L;
        FlashcardCreationDTO testDto = new FlashcardCreationDTO();
        testDto.setFront("front1");
        testDto.setBack("back1");
        testDto.setPartOfSpeech(PartOfSpeech.NOUN);
        testDto.setDynamicExamples(false);

        // when
        mockMvc.perform(post("/add-card?deckid=" + testDeckId)
                        .with(csrf())
                        .flashAttr("newFlashcard", testDto))
                // then
                .andExpect(status().isUnauthorized());
    }

}