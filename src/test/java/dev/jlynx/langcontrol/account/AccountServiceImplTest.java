package dev.jlynx.langcontrol.account;

import dev.jlynx.langcontrol.account.dto.AccountOverviewResponse;
import dev.jlynx.langcontrol.auth.dto.RegisterRequestBody;
import dev.jlynx.langcontrol.exception.UsernameAlreadyExistsException;
import dev.jlynx.langcontrol.role.DefinedRoleValue;
import dev.jlynx.langcontrol.role.Role;
import dev.jlynx.langcontrol.role.RoleRepository;
import dev.jlynx.langcontrol.usersettings.UserSettingsRepository;
import dev.jlynx.langcontrol.util.AuthRetriever;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    private static final String username = "existing";
    private static final String pwd = "aBcd12#4";
    private static final String pwd2 = "aBt3%4gh45srSui";

    @InjectMocks
    private AccountServiceImpl underTest;

    @Mock
    private AccountRepository mockedAccountRepository;
    @Mock
    private RoleRepository mockedRoleRepository;
    @Mock
    private PasswordEncoder mockedPasswordEncoder;
    @Mock
    private UserSettingsRepository mockedUserSettingsRepository;
    @Mock
    private AuthRetriever mockedAuthRetriever;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;


    @Test
    void registerAccount_ShouldThrowException_WhenAccountAlreadyExists() {
        // given
        RegisterRequestBody body = new RegisterRequestBody(username, pwd, "John Doe");
        Account existingAccount = new Account(
                7L,
                username,
                pwd2,
                List.of(new Role(1L, DefinedRoleValue.USER)),
                true, true,
                true, true
        );
        given(mockedAccountRepository.findByUsername(username))
                .willReturn(Optional.of(existingAccount));
        RuntimeException expectedException = null;

        // when
        try {
            underTest.registerAccount(body);
        } catch (RuntimeException e) {
            expectedException = e;
        }

        // then
        assertTrue(expectedException instanceof UsernameAlreadyExistsException);
    }

    @Test
    void registerAccount_ShouldSaveAccount_WhenAccountDoesNotAlreadyExistAndDataIsValid() {
        // given
        long accountId = 34L;
        String pwdEncoded = "test_encoded_pass";
        String expectedRoleValue = DefinedRoleValue.USER.getValue();
        Role role = new Role(1L, DefinedRoleValue.USER);
        RegisterRequestBody body = new RegisterRequestBody(username, pwd, "John Doe");
        given(mockedAccountRepository.findByUsername(username))
                .willReturn(Optional.empty());
        given(mockedPasswordEncoder.encode(pwd)).willReturn(pwdEncoded);
        given(mockedRoleRepository.findByValue(expectedRoleValue)).willReturn(Optional.of(role));
        given(mockedAccountRepository.save(any(Account.class))).willAnswer(new Answer<Account>() {
            @Override
            public Account answer(InvocationOnMock invocation) throws Throwable {
                Account account = invocation.getArgument(0);
                account.setId(accountId);
                return account;
            }
        });

        // when
        AccountOverviewResponse returned = underTest.registerAccount(body);

        // then
        InOrder inOrder = inOrder(mockedAccountRepository, mockedPasswordEncoder);
        then(mockedPasswordEncoder).should(inOrder, times(1)).encode(pwd);
        then(mockedAccountRepository).should(inOrder, times(1)).save(accountCaptor.capture());

        Account accountArg = accountCaptor.getValue();
        assertNotNull(accountArg.getId());
        assertEquals(username, accountArg.getUsername());
        assertEquals(pwdEncoded, accountArg.getPassword());
        assertTrue(accountArg.isEnabled());
        assertTrue(accountArg.isAccountNonLocked());
        assertNotNull(accountArg.getUserProfile());
        assertEquals(body.firstName(), accountArg.getUserProfile().getFirstName());
        assertEquals(1, accountArg.getRoles().size());
        assertEquals(expectedRoleValue, accountArg.getRoles().get(0).getValue());
        assertEquals(role.getId(), accountArg.getRoles().get(0).getId());
        assertEquals(accountArg.getId(), returned.id());
        assertEquals(accountArg.getUsername(), returned.username());
        assertTrue(returned.enabled());
        assertTrue(returned.accountNonLocked());
    }
}