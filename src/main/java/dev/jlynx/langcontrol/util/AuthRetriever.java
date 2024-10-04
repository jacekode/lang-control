package dev.jlynx.langcontrol.util;

import dev.jlynx.langcontrol.account.AccountRepository;
import dev.jlynx.langcontrol.exception.AuthenticationNotFoundException;
import dev.jlynx.langcontrol.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Contains a single method {@code retrieveAccount()} that fetches the currently authenticated user {@link Account}.
 */
@Service
public class AuthRetriever {

    public static final Logger LOG = LoggerFactory.getLogger(AuthRetriever.class);

    private final AccountRepository accountRepository;

    @Autowired
    public AuthRetriever(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Retrieves the current {@link Authentication} from the {@link SecurityContextHolder} and
     * fetches the corresponding {@link Account} object.
     *
     * @return currently authenticated {@link Account}
     */
    public Account retrieveCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOG.debug("SecurityContext doesn't hold an Authentication object.");
            throw new AuthenticationNotFoundException("You must be logged in to perform this action.");
        }
        LOG.debug("Fetched Authentication with name={}", authentication.getName());
        return accountRepository.findByUsername(authentication.getName()).orElseThrow(() ->
                new UsernameNotFoundException(
                        "The Account object with username %s could not be found".formatted(authentication.getName())
                )
        );
    }
}
