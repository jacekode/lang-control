package langcontrol.app.security;

import langcontrol.app.account.Account;
import langcontrol.app.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl underTest;

    @Mock
    private AccountRepository mockedAccountRepository;

    @BeforeEach
    void setUp() {
        this.underTest = new UserDetailsServiceImpl(mockedAccountRepository);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserIsNotFound() {
        // given
        String validEmail = "valid@example.com";
        RuntimeException expectedException = null;
        given(mockedAccountRepository.findByUsername(Mockito.anyString())).willReturn(Optional.empty());

        // when
        try {
            underTest.loadUserByUsername(validEmail);
        } catch (RuntimeException e) {
            expectedException = e;
        }

        // then
        assertTrue(expectedException instanceof UsernameNotFoundException);
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_WhenFound() {
        // given
        String validEmail = "valid@example.com";
        Account testAccount = new Account(
                7L, "valid@example.com",
                "aBt3%4gh45srSuiae5h%EA5h",
                List.of(new Role(1L, DefinedRoleValue.USER)),
                true, true,
                true, true);
        given(mockedAccountRepository.findByUsername(validEmail)).willReturn(Optional.of(testAccount));

        // when
        UserDetails result = underTest.loadUserByUsername(validEmail);

        // then
        then(mockedAccountRepository).should().findByUsername(validEmail);
        assertEquals(testAccount, result);
    }
}