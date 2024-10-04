package dev.jlynx.langcontrol.exception;

public class GeneralBadRequestException extends RuntimeException {

    public GeneralBadRequestException() {
    }

    public GeneralBadRequestException(String message) {
        super(message);
    }
}
