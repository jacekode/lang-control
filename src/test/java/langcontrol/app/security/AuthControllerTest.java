package langcontrol.app.security;

import langcontrol.app.account.AccountRegistrationDTO;
import langcontrol.app.account.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AccountService mockedAccountService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext).build();
    }

    @WithAnonymousUser
    @Test
    void getSignInPage_ShouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/signin"))
                .andExpect(view().name("signin-page"));
    }

    @WithAnonymousUser
    @Test
    void getSignUpPage_ShouldReturnRegisterPage() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(view().name("signup-page"))
                .andExpect(model().attribute("registrationData",
                        instanceOf(AccountRegistrationDTO.class)));
    }

    @WithAnonymousUser
    @Test
    void registerAccount_ShouldRegisterANewAccount_WhenDataIsValid() throws Exception {
        // given
        AccountRegistrationDTO validRegistrationDto = new AccountRegistrationDTO(
                "test.mail1@example.com", "aBcd12#4", "John Doe");

        // when
        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .flashAttr("registrationData", validRegistrationDto))
                .andExpect(view().name("redirect:/"));

        // then
        then(mockedAccountService).should(times(1))
                .registerNewUserAccount(validRegistrationDto);
    }

    @Disabled
    @Test
    void registerAccount_ShouldReturnBadRequestStatus_WhenDataIsInvalid() {
    }
}