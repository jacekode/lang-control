package langcontrol.app.exception;

public class AccessNotAllowedException extends RuntimeException {

    public AccessNotAllowedException() {
    }

    public AccessNotAllowedException(String message) {
        super(message);
    }
}
