package langcontrol.app.util;

import langcontrol.app.exception.AuthenticationNotFoundException;
import langcontrol.app.account.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class PrincipalRetriever {

    public static Account retrieveAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationNotFoundException("You must be logged in to perform this action.");
        }
        return (Account) authentication.getPrincipal();
    }

}
