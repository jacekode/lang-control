package dev.jlynx.langcontrol.spacedrepetition;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlynx.langcontrol.flashcard.WordFlashcardService;
import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingRequest;
import dev.jlynx.langcontrol.spacedrepetition.dto.FlashcardRatingResponse;
import dev.jlynx.langcontrol.url.SortOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpacedRepetitionController.class)
class SpacedRepetitionControllerTest {

    private static final String baseUrl = "/api/sr";
    private static final String username = "username";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper json;

    @MockBean
    private WordFlashcardService mockedWordFlashcardService;

    @MockBean
    private SpacedRepetitionService mockedSpacedRepetitionService;


    @WithAnonymousUser
    @Test
    void getReadyForViewCardsByDeck_ShouldReturnUnauthorizedStatusCode_WhenUserNotLoggedIn() throws Exception {
        mockMvc.perform(get(baseUrl)
                        .param("deck", "1")
                )
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = username)
    @Test
    void getReadyForViewCardsByDeck_ShouldPassOnDefaultQueryParamValues() throws Exception {
        // given
        long deckId = 16;
//        given(mockedWordFlashcardService.fetchAllReadyForViewByDeck(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
//                .willReturn(new ArrayList<>());

        // when, then
        mockMvc.perform(get(baseUrl)
                        .param("deck", String.valueOf(deckId))
                )
                .andExpect(status().isOk());

        // then
        then(mockedWordFlashcardService)
                .should(times(1))
                .fetchReadyForView(deckId, 10, SpacedRepetitionSortBy.NEXT_VIEW, SortOrder.ASC);
    }

    @WithMockUser(username = username)
    @ParameterizedTest
    @MethodSource("invalidQueryParams")
    void getReadyForViewCardsByDeck_ShouldReturnBadRequestStatusCode_WhenQueryParamsAreInvalid(
            long deck,
            int limit,
            String sort,
            String order
    ) throws Exception {
        // when, then
        mockMvc.perform(get(baseUrl)
                        .param("deck", String.valueOf(deck))
                        .param("limit", String.valueOf(limit))
                        .param("sort", sort)
                        .param("order", order)
                )
                .andExpect(status().isBadRequest());

        // then
        then(mockedWordFlashcardService).shouldHaveNoInteractions();
    }

    @WithMockUser(username = username)
    @ParameterizedTest
    @MethodSource("validQueryParams")
    void getReadyForViewCardsByDeck_ShouldPassOnValidQueryParams(
            long deck,
            int limit,
            String sort,
            String order
    ) throws Exception {
        // when, then
        mockMvc.perform(get(baseUrl)
                        .param("deck", String.valueOf(deck))
                        .param("limit", String.valueOf(limit))
                        .param("sort", sort)
                        .param("order", order)
                )
                .andExpect(status().isOk());

        // then
        then(mockedWordFlashcardService)
                .should(times(1))
                .fetchReadyForView(deck, limit, SpacedRepetitionSortBy.fromUrlValue(sort), SortOrder.fromUrlValue(order));
    }


    @WithAnonymousUser
    @Test
    void handleFlashcardRating_ShouldReturnUnauthorizedStatusCode_WhenUserNotLoggedIn() throws Exception {
        // given
        FlashcardRatingRequest body = new FlashcardRatingRequest(5, RatingType.LEARN_KNOW);

        // when, then
        mockMvc.perform(post(baseUrl + "/rating")
                        .content(json.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = username)
    @ParameterizedTest
    @MethodSource("invalidFlashcardRatingRequests")
    void handleFlashcardRating_ShouldReturnBadRequestStatusCode_WhenRequestBodyInvalid(FlashcardRatingRequest invalidBody) throws Exception {
        // when, then
        mockMvc.perform(post(baseUrl + "/rating")
                        .content(json.writeValueAsString(invalidBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = username)
    @Test
    void handleFlashcardRating_ShouldPassValidRequestBody() throws Exception {
        // given
        FlashcardRatingRequest body = new FlashcardRatingRequest(5, RatingType.REVIEW_REMEMBER);
        FlashcardRatingResponse expectedResponse = new FlashcardRatingResponse(5, false, false);
        given(mockedSpacedRepetitionService.applyRating(any(), any())).willReturn(expectedResponse);

        // when, then
        MvcResult mvcResult = mockMvc.perform(post(baseUrl + "/rating")
                        .content(json.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andReturn();

        // then
        then(mockedSpacedRepetitionService)
                .should(times(1))
                .applyRating(body.cardId(), body.rating());
        assertEquals(expectedResponse, json.readValue(mvcResult.getResponse().getContentAsString(), FlashcardRatingResponse.class));
    }


    static Stream<Arguments> invalidQueryParams() {
        return Stream.of(
                Arguments.of(0, 5, "interval", "desc"),
                Arguments.of(-1, 5, "interval", "desc"),
                Arguments.of(1, 2, "interval", "desc"),
                Arguments.of(1, 5, "abc", "desc"),
                Arguments.of(1, 5, "interval", "abc")
        );
    }

    static Stream<Arguments> validQueryParams() {
        return Stream.of(
                Arguments.of(1, 3, "view", "asc"),
                Arguments.of(99, 3, "view", "asc"),
                Arguments.of(99, 25, "view", "asc"),
                Arguments.of(1, 3, "created", "asc"),
                Arguments.of(1, 3, "interval", "asc"),
                Arguments.of(1, 3, "view", "desc")
        );
    }

    static Stream<FlashcardRatingRequest> invalidFlashcardRatingRequests() {
        return Stream.of(
                new FlashcardRatingRequest(0, RatingType.REVIEW_PARTIALLY),
                new FlashcardRatingRequest(-1, RatingType.LEARN_KNOW),
                new FlashcardRatingRequest(5, null)
        );
    }
}