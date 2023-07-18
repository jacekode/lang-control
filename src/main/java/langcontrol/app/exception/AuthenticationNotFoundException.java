package langcontrol.app.exception;

public class AuthenticationNotFoundException extends RuntimeException {

    public AuthenticationNotFoundException() {
    }

    public AuthenticationNotFoundException(String message) {
        super(message);
    }
}
