package langcontrol.app.account;

import langcontrol.app.account.Account;
import langcontrol.app.account.AccountRegistrationDTO;
import langcontrol.app.account.AccountRepository;
import langcontrol.app.account.AccountServiceImpl;
import langcontrol.app.exception.UsernameAlreadyExistsException;
import langcontrol.app.security.DefinedRoleValue;
import langcontrol.app.security.Role;
import langcontrol.app.security.RoleRepository;
import langcontrol.app.user_settings.UserSettingsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    private AccountServiceImpl underTest;

    @Mock
    private AccountRepository mockedAccountRepository;

    @Mock
    private RoleRepository mockedRoleRepository;

    @Mock
    private PasswordEncoder mockedPasswordEncoder;

    @Mock
    private UserSettingsRepository mockedUserSettingsRepository;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;


    @BeforeEach
    void setUp() {
        this.underTest = new AccountServiceImpl(mockedAccountRepository, mockedRoleRepository,
                mockedPasswordEncoder, mockedUserSettingsRepository);
    }

    @Test
    void registerNewUserAccount_ShouldThrowException_WhenAccountAlreadyExists() {
        // given
        AccountRegistrationDTO registrationDto = new AccountRegistrationDTO(
                "existing@example.com", "aBcd12#4a", "John Doe");
        Account existingAccount = new Account(
                7L, "existing@example.com",
                "aBt3%4gh45srSui",
                List.of(new Role(1L, DefinedRoleValue.USER)),
                true, true,
                true, true);
        given(mockedAccountRepository.findByUsername("existing@example.com"))
                .willReturn(Optional.of(existingAccount));
        RuntimeException expectedException = null;

        // when
        try {
            underTest.registerNewUserAccount(registrationDto);
        } catch (RuntimeException e) {
            expectedException = e;
        }

        // then
        assertTrue(expectedException instanceof UsernameAlreadyExistsException);
    }

    @Test
    void registerNewUserAccount_ShouldSaveNewUserAccount_WhenAccountDoesNotAlreadyExistAndDataIsValid() {
        // given
        String validEmail = "existing@example.com";
        String validPwd = "aBcd12#4a";
        String mockedPwdEncoded = "test_encoded_pass";
        String roleValue = DefinedRoleValue.USER.getValue();
        Role mockedRole = new Role(1L, DefinedRoleValue.USER);
        AccountRegistrationDTO registrationDto = new AccountRegistrationDTO(
                validEmail, validPwd, "John Doe");
        given(mockedAccountRepository.findByUsername(validEmail))
                .willReturn(Optional.empty());
        given(mockedPasswordEncoder.encode(validPwd)).willReturn(mockedPwdEncoded);
        given(mockedRoleRepository.findByValue(roleValue)).willReturn(Optional.of(mockedRole));

        // when
        underTest.registerNewUserAccount(registrationDto);

        // then
        InOrder inOrder = inOrder(mockedAccountRepository, mockedPasswordEncoder);
        then(mockedPasswordEncoder).should(inOrder, times(1)).encode(validPwd);
        then(mockedAccountRepository).should(inOrder, times(1)).save(accountCaptor.capture());

        Account accountArg = accountCaptor.getValue();
        assertEquals(validEmail, accountArg.getUsername());
        assertEquals(mockedPwdEncoded, accountArg.getPassword());
        Assertions.assertEquals(registrationDto.getName(), accountArg.getUserProfile().getName());
        assertEquals(1, accountArg.getRoles().size());
        assertEquals(roleValue, accountArg.getRoles().get(0).getValue());
        assertEquals(mockedRole.getId(), accountArg.getRoles().get(0).getId());
    }
}