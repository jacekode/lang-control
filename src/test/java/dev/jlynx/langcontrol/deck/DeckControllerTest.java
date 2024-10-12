package dev.jlynx.langcontrol.deck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlynx.langcontrol.deck.dto.CreateDeckRequest;
import dev.jlynx.langcontrol.deck.dto.DeckOverview;
import dev.jlynx.langcontrol.deck.view.DeckView;
import dev.jlynx.langcontrol.lang.LanguageCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeckController.class)
//@AutoConfigureMockMvc
class DeckControllerTest {

    private static final String baseUrl = "/api/decks";
    private static final String username = "username";
    private static final String deckName = "Test deck";
    private static final CreateDeckRequest validReqBody = new CreateDeckRequest(deckName, LanguageCode.SPANISH, LanguageCode.ENGLISH);
    private static final DeckOverview deckOverview = new DeckOverview(7L, deckName, LanguageCode.CZECH, LanguageCode.DUTCH);

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper json;

    @MockBean
    private DeckService mockedDeckService;

    @Captor
    private ArgumentCaptor<CreateDeckRequest> createDeckRequestCaptor;


//    @BeforeEach
//    void setUp() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
//    }


    @WithMockUser(username = username)
    @Test
    void createDeck_ShouldCreateAndReturnOverview() throws Exception {
        // given
        given(mockedDeckService.createNewDeck(Mockito.any(CreateDeckRequest.class))).willReturn(deckOverview);

        // when
        MvcResult mvcResult = mockMvc.perform(post(baseUrl)
                        .content(json.writeValueAsString(validReqBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andReturn();

        // then
        then(mockedDeckService).should(times(1)).createNewDeck(createDeckRequestCaptor.capture());
        CreateDeckRequest arg = createDeckRequestCaptor.getValue();
        assertEquals(validReqBody.name(), arg.name());
        assertEquals(validReqBody.sourceLang(), arg.sourceLang());
        assertEquals(validReqBody.targetLang(), arg.targetLang());
        DeckOverview responseBody = json.readValue(mvcResult.getResponse().getContentAsString(), DeckOverview.class);
        assertEquals(deckOverview, responseBody);
    }

    @WithAnonymousUser
    @Test
    void createDeck_ShouldReturnUnauthorizedStatusCode_WhenUserNotAuthenticated() throws Exception {
        // when, then
        mockMvc.perform(post(baseUrl)
                        .content(json.writeValueAsString(validReqBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());

        // then
        then(mockedDeckService).shouldHaveNoInteractions();
    }

    @WithMockUser(username = username)
    @ParameterizedTest
    @MethodSource("invalidCreateDeckRequests")
    void createDeck_ShouldReturnBadRequest_WhenRequestBodyInvalid(CreateDeckRequest invalid) throws Exception {
        // given

        // when, then
        mockMvc.perform(post(baseUrl)
                        .content(json.writeValueAsString(invalid))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isBadRequest());

        // then
        then(mockedDeckService).shouldHaveNoInteractions();
    }

    @WithMockUser(username = username)
    @Test
    void getAllUserDecks_ShouldReturnAllDecksOfCurrentUser() throws Exception {
        // given
        DeckView deckView1 = new DeckView(67L, "Deck One", LanguageCode.SPANISH, LanguageCode.ENGLISH);
        DeckView deckView2 = new DeckView(72L, "Deck Two", LanguageCode.FRENCH, LanguageCode.GERMAN);
        List<DeckView> deckViewList = List.of(deckView1, deckView2);
        given(mockedDeckService.getAllCurrentUserProfileDecks()).willReturn(deckViewList);

        // when + then
        MvcResult mvcResult = mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andReturn();

        // then
        then(mockedDeckService).should(times(1)).getAllCurrentUserProfileDecks();
        List<DeckView> response = json.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<DeckView>>() {});
        assertEquals(deckViewList, response);
    }

    @WithAnonymousUser
    @Test
    void getAllUserDecks_ShouldReturnUnauthorizedStatusCode_WhenUserNotAuthenticated() throws Exception {
        // when, then
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isUnauthorized());

        // then
        then(mockedDeckService).shouldHaveNoInteractions();
    }

    @WithMockUser(username = username)
    @Test
    void deleteDeck_ShouldDeleteDeck() throws Exception {
        // given
        long deckId = 145L;

        // when, then
        mockMvc.perform(delete(String.format("%s/%d", baseUrl, deckId))
                        .with(csrf())
                )
                .andExpect(status().isNoContent());

        // then
        then(mockedDeckService).should(times(1)).deleteDeck(deckId);
    }

    @WithMockUser(username = username)
    @ParameterizedTest
    @ValueSource(longs = { 0L, -1L})
    void deleteDeck_ShouldReturnBadRequestStatusCode_WhenDeckIdLessThanOne(long invalidId) throws Exception {
        // when, then
        mockMvc.perform(delete(String.format("%s/%d", baseUrl, invalidId))
                        .with(csrf())
                )
                .andExpect(status().isBadRequest());

        // then
        then(mockedDeckService).shouldHaveNoInteractions();
    }

    @WithAnonymousUser
    @Test
    void deleteDeck_ShouldReturnUnauthorizedStatusCode_WhenUserNotAuthenticated() throws Exception {
        // given
        long deckId = 145L;

        // when, then
        mockMvc.perform(delete(String.format("%s/%d", baseUrl, deckId))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());

        // then
        then(mockedDeckService).shouldHaveNoInteractions();
    }

    static Stream<CreateDeckRequest> invalidCreateDeckRequests() {
        return Stream.of(
                new CreateDeckRequest("longname longname longname longname", LanguageCode.CZECH, LanguageCode.DANISH),
                new CreateDeckRequest("   ", LanguageCode.CZECH, LanguageCode.DANISH),
                new CreateDeckRequest(null, LanguageCode.CZECH, LanguageCode.DANISH),
                new CreateDeckRequest("Test deck", null, LanguageCode.DANISH),
                new CreateDeckRequest("Test deck", LanguageCode.DANISH, null)
        );
    }
}
