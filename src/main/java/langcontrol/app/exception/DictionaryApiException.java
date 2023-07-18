package langcontrol.app.exception;

public class DictionaryApiException extends RuntimeException {
    public DictionaryApiException() {
        super();
    }

    public DictionaryApiException(String message) {
        super(message);
    }
}
