package langcontrol.app.exception;

public class OpenAiTranslationErrorException extends RuntimeException {

    public OpenAiTranslationErrorException() {
        super();
    }

    public OpenAiTranslationErrorException(String message) {
        super(message);
    }
}
