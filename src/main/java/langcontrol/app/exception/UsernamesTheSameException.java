package langcontrol.app.exception;

public class UsernamesTheSameException extends RuntimeException {

    public UsernamesTheSameException() {
    }

    public UsernamesTheSameException(String message) {
        super(message);
    }
}
