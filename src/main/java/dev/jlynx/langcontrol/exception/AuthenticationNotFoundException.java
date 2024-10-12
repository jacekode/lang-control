package dev.jlynx.langcontrol.exception;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Thrown when no {@link Authentication} object is present in the {@link SecurityContextHolder}.
 */
public class AuthenticationNotFoundException extends RuntimeException {

    public AuthenticationNotFoundException() {
    }

    public AuthenticationNotFoundException(String message) {
        super(message);
    }
}
