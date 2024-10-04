package dev.jlynx.langcontrol.util;

import dev.jlynx.langcontrol.exception.AuthenticationNotFoundException;
import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.role.DefinedRoleValue;
import dev.jlynx.langcontrol.role.Role;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

class AuthRetrieverTest {

    @Test
    void retrieveCurrentAccount_ShouldThrowException_WhenAuthenticationIsNull() {
        // given
        RuntimeException expectedException = null;
        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        given(mockedSecurityContext.getAuthentication()).willReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedStaticSCH = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStaticSCH.when(SecurityContextHolder::getContext).thenReturn(mockedSecurityContext);

        // when
            try {
                AuthRetriever.retrieveCurrentAccount();
            } catch (RuntimeException e) {
                expectedException = e;
            }
        }

        // then
        assertTrue(expectedException instanceof AuthenticationNotFoundException);
    }

    @Test
    void retrieveAccount_ShouldReturnCurrentAccount_WhenAuthenticationIsNotNull() {
        // given
        Account retrievedAccount;
        Account testAccount = new Account(17L,
                "test@example.com",
                "78oytAb$HEby7o",
                List.of(new Role(1L, DefinedRoleValue.USER)));

        SecurityContext mockedSecurityContext = Mockito.mock(SecurityContext.class);
        Authentication mockedAuthentication = Mockito.mock(Authentication.class);
        given(mockedSecurityContext.getAuthentication()).willReturn(mockedAuthentication);
        given(mockedAuthentication.getPrincipal()).willReturn(testAccount);

        try (MockedStatic<SecurityContextHolder> mockedStaticSCH = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStaticSCH.when(SecurityContextHolder::getContext).thenReturn(mockedSecurityContext);

            // when
            retrievedAccount = AuthRetriever.retrieveCurrentAccount();
        }

        // then
        assertEquals(testAccount.getId(), retrievedAccount.getId());
        assertEquals(testAccount.getUsername(), retrievedAccount.getUsername());
        assertEquals(testAccount.getPassword(), retrievedAccount.getPassword());
        assertEquals(testAccount.getAuthorities(), retrievedAccount.getAuthorities());
        assertEquals(testAccount.getRoles(), retrievedAccount.getRoles());
    }
}