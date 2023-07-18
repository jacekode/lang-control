package langcontrol.app.exception;

public class DeckCreationException extends RuntimeException {

    public DeckCreationException() {
    }

    public DeckCreationException(String message) {
        super(message);
    }
}
