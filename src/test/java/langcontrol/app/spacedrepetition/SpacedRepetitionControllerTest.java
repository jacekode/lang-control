package langcontrol.app.spacedrepetition;

import langcontrol.app.deck.Deck;
import langcontrol.app.deck.LanguageCode;
import langcontrol.app.flashcard.WordFlashcard;
import langcontrol.app.flashcard.WordFlashcardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpacedRepetitionController.class)
class SpacedRepetitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordFlashcardService mockedWordFlashcardService;

    @MockBean
    private SpacedRepetitionService mockedSpacedRepetitionService;

    public static Stream<Arguments> invalidHandleFlashcardRatingMethodParams() {
        return Stream.of(
                Arguments.arguments(new FlashcardRatingDTO(null, 2L), new ArrayDeque<>()),
                Arguments.arguments(new FlashcardRatingDTO(RatingType.LEARN_NEXT, null), new ArrayDeque<>()),
                Arguments.arguments(new FlashcardRatingDTO(RatingType.LEARN_NEXT, 0L), new ArrayDeque<>())
        );
    }

    public static ArrayDeque<WordFlashcard> threeElementFlashcardArrayDequeFirstInLearnMode() {
        Deck testDeck = new Deck(54L, "test deck", null,
                LanguageCode.ENGLISH, LanguageCode.CZECH, new ArrayList<>());

        WordFlashcard card1 = WordFlashcard.inInitialLearnMode()
                .withFront("front 1")
                .withBack("back 1")
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.GERMAN)
                .withDeck(testDeck)
                .build();
        WordFlashcard card2 = WordFlashcard.inInitialReviewMode()
                .withFront("front 2")
                .withBack("back 2")
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.SPANISH)
                .withDeck(testDeck)
                .build();
        WordFlashcard card3 = WordFlashcard.inInitialReviewMode()
                .withFront("front 3")
                .withBack("back 3")
                .withSourceLang(LanguageCode.SPANISH)
                .withTargetLang(LanguageCode.ENGLISH)
                .withDeck(testDeck)
                .build();
        testDeck.getFlashcards().add(card1);
        testDeck.getFlashcards().add(card2);
        testDeck.getFlashcards().add(card3);
        return new ArrayDeque<>(List.of(card1, card2, card3));
    }

    public static ArrayDeque<WordFlashcard> threeElementFlashcardArrayDequeFirstInReviewMode() {
        Deck testDeck = new Deck(54L, "test deck", null,
                LanguageCode.ENGLISH, LanguageCode.CZECH, new ArrayList<>());

        WordFlashcard card1 = WordFlashcard.inInitialReviewMode()
                .withFront("front 1")
                .withBack("back 1")
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.SPANISH)
                .withDeck(testDeck)
                .build();
        WordFlashcard card2 = WordFlashcard.inInitialLearnMode()
                .withFront("front 2")
                .withBack("back 2")
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.GERMAN)
                .withDeck(testDeck)
                .build();
        WordFlashcard card3 = WordFlashcard.inInitialReviewMode()
                .withFront("front 3")
                .withBack("back 3")
                .withSourceLang(LanguageCode.SPANISH)
                .withTargetLang(LanguageCode.ENGLISH)
                .withDeck(testDeck)
                .build();
        testDeck.getFlashcards().add(card1);
        testDeck.getFlashcards().add(card2);
        testDeck.getFlashcards().add(card3);
        return new ArrayDeque<>(List.of(card1, card2, card3));
    }

    public static ArrayDeque<WordFlashcard> oneElementFlashcardArrayDeque() {
        Deck testDeck = new Deck(54L, "test deck", null,
                LanguageCode.ENGLISH, LanguageCode.CZECH, new ArrayList<>());

        WordFlashcard card3 = WordFlashcard.inInitialReviewMode()
                .withFront("front 1")
                .withBack("back 1")
                .withSourceLang(LanguageCode.SPANISH)
                .withTargetLang(LanguageCode.ENGLISH)
                .withDeck(testDeck)
                .build();
        testDeck.getFlashcards().add(card3);
        return new ArrayDeque<>(List.of(card3));
    }


    @WithAnonymousUser
    @Test
    void openDeck_ShouldReturnStatusCodeUnauthorized_WhenUserIsAnonymous() throws Exception {
        mockMvc.perform(get("/review")
                        .param("deckId", "1")
                        .param("timezone", "Europe/Warsaw"))
                .andExpect(status().isUnauthorized());
    }


    @WithMockUser(username = "username")
    @Test
    void openDeck_ShouldRedirectToDecksPage_WhenReadyForReviewResultIsEmpty() throws Exception {
        // given
        given(mockedWordFlashcardService.fetchShuffleReadyForView(
                Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt()))
                .willReturn(new ArrayDeque<>());

        // when
        MvcResult mvcResult = mockMvc.perform(get("/review")
                        .param("deckId", "1")
                        .param("timezone", "Europe/Warsaw"))
                .andExpect(status().isFound())
                .andReturn();

        // then
        then(mockedWordFlashcardService).should(times(1))
                .fetchShuffleReadyForView(
                        Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt());
        assertEquals("redirect:/decks", Objects.requireNonNull(mvcResult.getModelAndView()).getViewName());
    }


    @WithMockUser(username = "username")
    @ParameterizedTest
    @CsvSource(value = {
            "0,Europe/Warsaw",
            "-1,Europe/Warsaw",
            "1, ",
            "1,'  '"},
            ignoreLeadingAndTrailingWhitespace = false)
    void openDeck_ShouldReturnBadRequestStatusCode_WhenParametersAreNotValid(long deckIdParam,
                                                                         String timezoneIdParam) throws Exception {
        mockMvc.perform(get("/review")
                        .param("deckId", String.valueOf(deckIdParam))
                        .param("timezone", timezoneIdParam))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "username")
    @Test
    void openDeck_ShouldDisplayLearnPage_WhenTheFirstReadyForReviewCardIsInLearnMode() throws Exception {
        // given
        long deckId = 23L;
        String timezoneId = "America/Los_Angeles";
        int limit = 10;
        Deck testDeck = new Deck(34L, "test deck", null,
                LanguageCode.SPANISH, LanguageCode.BULGARIAN, new ArrayList<>());
        WordFlashcard cardLearnMode = WordFlashcard.inInitialLearnMode()
                .withFront("learn front 1")
                .withBack("learn back 1")
                .withDeck(testDeck)
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.SPANISH).build();
        cardLearnMode.setId(7L);
        WordFlashcard cardReviewMode1 = WordFlashcard.inInitialReviewMode()
                .withFront("review front 1")
                .withBack("review back 1")
                .withDeck(testDeck)
                .withSourceLang(LanguageCode.SPANISH)
                .withTargetLang(LanguageCode.GERMAN).build();
        cardReviewMode1.setId(36L);
        WordFlashcard cardReviewMode2 = WordFlashcard.inInitialReviewMode()
                .withFront("review front 2")
                .withBack("review back 2")
                .withDeck(testDeck)
                .withSourceLang(LanguageCode.SPANISH)
                .withTargetLang(LanguageCode.ENGLISH).build();
        testDeck.getFlashcards().add(cardLearnMode);
        testDeck.getFlashcards().add(cardReviewMode1);
        testDeck.getFlashcards().add(cardReviewMode2);

        LocalDateTime lastReview = LocalDateTime
                .of(2023, 3, 14, 16, 37, 21);
        cardReviewMode2.setLastReviewInUTC(lastReview);
        cardReviewMode2.setCurrentIntervalDays(11.6);
        cardReviewMode2.setNextReviewInUTC(lastReview.plusDays(12));
        cardReviewMode2.setNextReviewWithoutTimeInUTC(lastReview.plusDays(12).toLocalDate());
        cardReviewMode2.setId(76L);
        Deque<WordFlashcard> learnCardFirstDeque = new ArrayDeque<>(List.of(cardLearnMode, cardReviewMode1, cardReviewMode2));

        given(mockedWordFlashcardService.fetchShuffleReadyForView(deckId, timezoneId, limit))
                .willReturn(learnCardFirstDeque);

        // when
        mockMvc.perform(get("/review")
                        .param("deckId", String.valueOf(deckId))
                        .param("timezone", timezoneId))
                .andExpect(status().isOk())
                .andExpect(view().name("learn"))
                .andExpect(model().attribute("currentCard",
                        hasProperty("id", equalTo(7L))))
                .andExpect(model().attribute("reviewCards", hasSize(3)))
                .andExpect(model().attribute("reviewCards", allOf(
                        hasItem(equalTo(cardLearnMode)),
                        hasItem(equalTo(cardReviewMode1)),
                        hasItem(equalTo(cardReviewMode2))))
                );

        // then
        then(mockedWordFlashcardService)
                .should(times(1))
                .fetchShuffleReadyForView(deckId, timezoneId, limit);
    }

    @WithMockUser(username = "username")
    @Test
    void openDeck_ShouldDisplayReviewPage_WhenTheFirstReadyForReviewCardIsInReviewMode() throws Exception {
        // given
        long deckId = 23L;
        String timezoneId = "America/Los_Angeles";
        int limit = 10;
        Deck testDeck = new Deck(34L, "test deck", null,
                LanguageCode.SPANISH, LanguageCode.BULGARIAN, new ArrayList<>());
        WordFlashcard cardLearnMode = WordFlashcard.inInitialLearnMode()
                .withFront("learn front 1")
                .withBack("learn back 1")
                .withDeck(testDeck)
                .withSourceLang(LanguageCode.ENGLISH)
                .withTargetLang(LanguageCode.SPANISH).build();
        cardLearnMode.setId(7L);
        WordFlashcard cardReviewMode1 = WordFlashcard.inInitialReviewMode()
                .withFront("review front 1")
                .withBack("review back 1")
                .withDeck(testDeck)
                .withSourceLang(LanguageCode.SPANISH)
                .withTargetLang(LanguageCode.GERMAN).build();
        cardReviewMode1.setId(36L);
        WordFlashcard cardReviewMode2 = WordFlashcard.inInitialReviewMode()
                .withFront("review front 2")
                .withBack("review back 2")
                .withDeck(testDeck)
                .withSourceLang(LanguageCode.SPANISH)
                .withTargetLang(LanguageCode.ENGLISH).build();
        testDeck.getFlashcards().add(cardLearnMode);
        testDeck.getFlashcards().add(cardReviewMode1);
        testDeck.getFlashcards().add(cardReviewMode2);

        LocalDateTime lastReview = LocalDateTime
                .of(2023, 3, 14, 16, 37, 21);
        cardReviewMode2.setLastReviewInUTC(lastReview);
        cardReviewMode2.setCurrentIntervalDays(11.6);
        cardReviewMode2.setNextReviewInUTC(lastReview.plusDays(12));
        cardReviewMode2.setNextReviewWithoutTimeInUTC(lastReview.plusDays(12).toLocalDate());
        cardReviewMode2.setId(86L);
        Deque<WordFlashcard> reviewCardFirstDeque = new ArrayDeque<>(List.of(cardReviewMode2, cardLearnMode, cardReviewMode1));

        given(mockedWordFlashcardService.fetchShuffleReadyForView(deckId, timezoneId, limit))
                .willReturn(reviewCardFirstDeque);

        // when
        mockMvc.perform(get("/review")
                        .param("deckId", String.valueOf(deckId))
                        .param("timezone", timezoneId))
                .andExpect(status().isOk())
                .andExpect(view().name("review"))
                .andExpect(model().attribute("currentCard",
                        hasProperty("id", equalTo(86L))
                ))
                .andExpect(model().attribute("reviewCards", hasSize(3)))
                .andExpect(model().attribute("reviewCards", allOf(
                        hasItem(equalTo(cardLearnMode)),
                        hasItem(equalTo(cardReviewMode1)),
                        hasItem(equalTo(cardReviewMode2)))
                ));

        // then
        then(mockedWordFlashcardService)
                .should(times(1))
                .fetchShuffleReadyForView(deckId, timezoneId, limit);
    }

    @WithMockUser(username = "username")
    @Test
    void reviewNextCard_ShouldDisplayLearnPage_WhenTheTopReadyForReviewCardIsInLearnMode() throws Exception {
        // given
        Deque<WordFlashcard> readyForReviewCardsDeque = threeElementFlashcardArrayDequeFirstInLearnMode();

        // when
        mockMvc.perform(get("/review/next")
                        .flashAttr("reviewCards", readyForReviewCardsDeque))
        // then
                .andExpect(view().name("learn"))
                .andExpect(model().attribute("reviewCards", readyForReviewCardsDeque))
                .andExpect(model().attribute("currentCard", readyForReviewCardsDeque.element()))
                .andExpect(model().attribute("rating", instanceOf(FlashcardRatingDTO.class)));
    }

    @WithMockUser(username = "username")
    @Test
    void reviewNextCard_ShouldDisplayReviewPage_WhenTheTopReadyForReviewCardIsInReviewMode() throws Exception {
        // given
        Deque<WordFlashcard> readyForReviewCardsDeque = threeElementFlashcardArrayDequeFirstInReviewMode();

        // when
        mockMvc.perform(get("/review/next")
                        .flashAttr("reviewCards", readyForReviewCardsDeque))
        // then
                .andExpect(view().name("review"))
                .andExpect(model().attribute("reviewCards", readyForReviewCardsDeque))
                .andExpect(model().attribute("currentCard", readyForReviewCardsDeque.element()))
                .andExpect(model().attribute("rating", instanceOf(FlashcardRatingDTO.class)));
    }

    @WithMockUser(username = "username")
    @Test
    void reviewNextCard_ShouldRedirectToMainDecksPage_WhenReadyForReviewCardsDequeIsEmpty() throws Exception {
        // given
        Deque<WordFlashcard> readyForReviewCardsDeque = new ArrayDeque<>();

        // when
        mockMvc.perform(get("/review/next")
                        .flashAttr("reviewCards", readyForReviewCardsDeque))
                .andExpect(redirectedUrl("/decks"));
    }
}