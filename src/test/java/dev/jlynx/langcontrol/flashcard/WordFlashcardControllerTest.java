package dev.jlynx.langcontrol.flashcard;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlynx.langcontrol.flashcard.dto.CreateWordFlashcardRequest;
import dev.jlynx.langcontrol.flashcard.dto.WordFlashcardOverviewResponse;
import dev.jlynx.langcontrol.lang.LanguageCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WordFlashcardController.class)
class WordFlashcardControllerTest {

    private static final String baseUrl = "/api/cards";
    private static final String username = "username";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper json;

    @MockBean
    private WordFlashcardService mockedWordFlashcardService;


    @WithMockUser(username = username)
    @ParameterizedTest
    @MethodSource("validCreateWordFlashcardRequests")
    void createFlashcard_ShouldCreateFlashcard_WhenRequestBodyIsCorrect(CreateWordFlashcardRequest reqBody) throws Exception {
        // given
        WordFlashcardOverviewResponse response = new WordFlashcardOverviewResponse(
                5,
                reqBody.targetWord(),
                reqBody.translatedWord(),
                LanguageCode.FRENCH,
                LanguageCode.ENGLISH,
                reqBody.targetExample(),
                reqBody.translatedExample(),
                reqBody.partOfSpeech(),
                reqBody.dynamicExamples(),
                true,
                123,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(123),
                null
        );
        given(mockedWordFlashcardService.createNewFlashcard(reqBody)).willReturn(response);

        // when, then
        MvcResult mvcResult = mockMvc.perform(post(baseUrl)
                        .content(json.writeValueAsString(reqBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andReturn();

        // then
        then(mockedWordFlashcardService).should(times(1)).createNewFlashcard(reqBody);
        WordFlashcardOverviewResponse resBody = json.readValue(mvcResult.getResponse().getContentAsString(), WordFlashcardOverviewResponse.class);
        assertEquals(response, resBody);
    }

    @WithMockUser(username = username)
    @ParameterizedTest
    @MethodSource("invalidCreateWordFlashcardRequests")
    void createFlashcard_ShouldReturnBadRequestStatusCode_WhenRequestBodyInvalid(CreateWordFlashcardRequest reqBody) throws Exception {
        // when, then
        mockMvc.perform(post(baseUrl)
                        .content(json.writeValueAsString(reqBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isBadRequest());

        // then
        then(mockedWordFlashcardService).shouldHaveNoInteractions();
    }

    @WithAnonymousUser
    @Test
    void createNewFlashcard_ShouldReturnUnauthorizedStatusCode_WhenUserIsNotAuthenticated() throws Exception {
        // given
        CreateWordFlashcardRequest validReqBody = new CreateWordFlashcardRequest(
                "translation",
                "target",
                PartOfSpeech.NOUN,
                true,
                "This is a translated example.",
                "This is an example sentence.",
                23L
        );

        // when, then
        mockMvc.perform(post(baseUrl)
                        .content(json.writeValueAsString(validReqBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());

        // then
        then(mockedWordFlashcardService).shouldHaveNoInteractions();
    }

    static Stream<CreateWordFlashcardRequest> validCreateWordFlashcardRequests() {
        long deckId = 94L;
        CreateWordFlashcardRequest body1 = new CreateWordFlashcardRequest(
                "translation",
                "target",
                PartOfSpeech.NOUN,
                true,
                "This is a translated example.",
                "This is an example sentence.",
                deckId
        );
        CreateWordFlashcardRequest body2 = new CreateWordFlashcardRequest(
                "translation",
                "target",
                null,
                true,
                "This is a translated example.",
                "This is an example sentence.",
                deckId
        );
        return Stream.of(body1, body2);
    }

    static Stream<CreateWordFlashcardRequest> invalidCreateWordFlashcardRequests() {
        return Stream.of(
                new CreateWordFlashcardRequest(
                        "translationtranslationtranslationtranslationtranslationtranslationtranslationtran",
                        "target",
                        PartOfSpeech.NOUN,
                        true,
                        "This is a translated example.",
                        "This is an example sentence.",
                        14L
                ),
                new CreateWordFlashcardRequest(
                        "translation",
                        "targettargettargettargettargettargettargettargettargettargettargettargettargettar",
                        null,
                        true,
                        "This is a translated example.",
                        "This is an example sentence.",
                        14L
                ),
                new CreateWordFlashcardRequest(
                        "translation",
                        "target",
                        null,
                        true,
                        "This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is aa.",
                        "This is an example sentence.",
                        14L
                ),
                new CreateWordFlashcardRequest(
                        "translation",
                        "target",
                        null,
                        true,
                        "This is a translated example.",
                        "This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is a translated example.This is aa.",
                        14L
                ),
                new CreateWordFlashcardRequest(
                        "translation",
                        "target",
                        null,
                        true,
                        null,
                        "This is an example sentence.",
                        14L
                ),
                new CreateWordFlashcardRequest(
                        "translation",
                        "target",
                        null,
                        true,
                        "This is a translated example.",
                        null,
                        14L
                ),
                new CreateWordFlashcardRequest(
                        "translation",
                        "target",
                        null,
                        true,
                        "This is a translated example.",
                        "This is a translated example.",
                        0L
                )
        );
    }
}