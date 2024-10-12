package dev.jlynx.langcontrol.exception;

/**
 * Thrown when the submitted password is incorrect.
 */
public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException() {
    }

    public WrongPasswordException(String message) {
        super(message);
    }
}
