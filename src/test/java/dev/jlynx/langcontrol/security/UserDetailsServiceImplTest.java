package dev.jlynx.langcontrol.security;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.account.AccountRepository;
import dev.jlynx.langcontrol.role.DefinedRoleValue;
import dev.jlynx.langcontrol.role.Role;
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
import static org.mockito.Mockito.times;

class UserDetailsServiceImplTest {

    private static final String validUsername = "username";

    private UserDetailsServiceImpl underTest;

    private AccountRepository mockedAccountRepository;

    @BeforeEach
    void setUp() {
        mockedAccountRepository = Mockito.mock(AccountRepository.class);
        this.underTest = new UserDetailsServiceImpl(mockedAccountRepository);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserIsNotFound() {
        // given
        RuntimeException expectedException = null;
        given(mockedAccountRepository.findByUsername(Mockito.anyString())).willReturn(Optional.empty());

        // when
        try {
            underTest.loadUserByUsername(validUsername);
        } catch (RuntimeException e) {
            expectedException = e;
        }

        // then
        assertInstanceOf(UsernameNotFoundException.class, expectedException);
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_WhenFound() {
        // given
        Account testAccount = new Account(
                7L,
                validUsername,
                "aBt3%4gh45srSuiae5h%EA5h",
                List.of(new Role(1L, DefinedRoleValue.USER)),
                true, true,
                true, true
        );
        given(mockedAccountRepository.findByUsername(validUsername)).willReturn(Optional.of(testAccount));

        // when
        UserDetails returned = underTest.loadUserByUsername(validUsername);

        // then
        then(mockedAccountRepository).should(times(1)).findByUsername(validUsername);
        assertEquals(testAccount, returned);
    }
}