package langcontrol.app.exception;

public class InvalidDatabaseValueException extends RuntimeException {

    public InvalidDatabaseValueException() {
    }

    public InvalidDatabaseValueException(String message) {
        super(message);
    }
}
