package dev.jlynx.langcontrol.util;

import dev.jlynx.langcontrol.account.AccountRepository;
import dev.jlynx.langcontrol.exception.AuthenticationNotFoundException;
import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.role.DefinedRoleValue;
import dev.jlynx.langcontrol.role.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class AuthRetrieverTest {

    private static final String username = "username";

    private AuthRetriever underTest;
    private AccountRepository mockedAccountRepository;
    SecurityContext mockedSecurityContext;
    Authentication mockedAuthentication;

    @BeforeEach
    void setUp() {
        mockedAccountRepository = Mockito.mock(AccountRepository.class);
        underTest = new AuthRetriever(mockedAccountRepository);
        mockedSecurityContext = Mockito.mock(SecurityContext.class);
        mockedAuthentication = Mockito.mock(Authentication.class);
    }

    @Test
    void retrieveCurrentAccount_ShouldThrow_WhenAuthenticationIsNull() {
        // given
        RuntimeException expectedException = null;
        given(mockedSecurityContext.getAuthentication()).willReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedStaticSCH = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStaticSCH.when(SecurityContextHolder::getContext).thenReturn(mockedSecurityContext);

        // when
            try {
                underTest.retrieveCurrentAccount();
            } catch (RuntimeException e) {
                expectedException = e;
            }
        }

        // then
        assertInstanceOf(AuthenticationNotFoundException.class, expectedException);
    }

    @Test
    void retrieveAccount_ShouldReturnCurrentAccount_WhenAuthenticationIsPresent() {
        // given
        Account returned;
        Account testAccount = new Account(17L,
                username,
                "78oytAb$HEby7o",
                List.of(new Role(1L, DefinedRoleValue.USER))
        );
        given(mockedSecurityContext.getAuthentication()).willReturn(mockedAuthentication);
        given(mockedAuthentication.getName()).willReturn(username);
        given(mockedAccountRepository.findByUsername(username)).willReturn(Optional.of(testAccount));

        try (MockedStatic<SecurityContextHolder> mockedStaticSCH = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStaticSCH.when(SecurityContextHolder::getContext).thenReturn(mockedSecurityContext);

            // when
            returned = underTest.retrieveCurrentAccount();
        }

        // then
        then(mockedAccountRepository).should(times(1)).findByUsername(username);
        assertEquals(testAccount.getId(), returned.getId());
        assertEquals(testAccount.getUsername(), returned.getUsername());
        assertEquals(testAccount.getPassword(), returned.getPassword());
        assertEquals(testAccount.getAuthorities(), returned.getAuthorities());
        assertEquals(testAccount.getRoles(), returned.getRoles());
    }
}