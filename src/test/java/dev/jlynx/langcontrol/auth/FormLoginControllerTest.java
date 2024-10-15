package dev.jlynx.langcontrol.auth;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlynx.langcontrol.account.AccountService;
import dev.jlynx.langcontrol.account.dto.AccountOverviewResponse;
import dev.jlynx.langcontrol.auth.dto.RegisterRequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FormLoginController.class)
class FormLoginControllerTest {

    private static final String baseUrl = "/api/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper json;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AccountService mockedAccountService;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @WithAnonymousUser
    @ParameterizedTest
    @MethodSource("validRegisterRequests")
    void registerAccount_ShouldRegister_WhenRequestBodyIsValid(RegisterRequestBody validReqBody) throws Exception {
        // given
        AccountOverviewResponse expectedResBody = new AccountOverviewResponse(12, "john", true, true, List.of("ROLE_USER"));
        given(mockedAccountService.registerAccount(any(RegisterRequestBody.class))).willReturn(expectedResBody);

        // when, then
        MvcResult mvcResult = mockMvc.perform(post(baseUrl + "/register")
                        .content(json.writeValueAsString(validReqBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andReturn();

        // then
        then(mockedAccountService).should(times(1)).registerAccount(validReqBody);
        String resBodyStr = mvcResult.getResponse().getContentAsString();
        AccountOverviewResponse resBody = json.readValue(resBodyStr, AccountOverviewResponse.class);
        assertEquals(expectedResBody, resBody);
    }

    static Stream<RegisterRequestBody> validRegisterRequests() {
        return Stream.of(
                new RegisterRequestBody("abcd", "P@ssw0rd", "Jo"),
                new RegisterRequestBody("_abcdabcdabcdabcd_abcdabcdabcd", "P@ssw0rd1", "John")
        );
    }
}