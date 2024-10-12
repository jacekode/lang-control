package dev.jlynx.langcontrol.exception;

/**
 * Thrown when a user has entered a username used by another account.
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException() {
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
