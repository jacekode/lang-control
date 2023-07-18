package langcontrol.app.exception;

public class GeneralNotFoundException extends RuntimeException {

    public GeneralNotFoundException() {
    }

    public GeneralNotFoundException(String message) {
        super(message);
    }
}
