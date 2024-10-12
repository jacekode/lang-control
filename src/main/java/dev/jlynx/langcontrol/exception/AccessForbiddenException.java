package dev.jlynx.langcontrol.exception;

/**
 * Thrown when a user is not authorized to access certain data.
 */
public class AccessForbiddenException extends RuntimeException {

    public AccessForbiddenException() {
    }

    public AccessForbiddenException(String message) {
        super(message);
    }
}
