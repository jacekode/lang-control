package dev.jlynx.langcontrol.exception;

import dev.jlynx.langcontrol.generator.openai.OpenaiClient;

/**
 * Thrown when the {@link com.deepl.api.Translator} returns a 4xx or 5xx status code as a response.
 */
public class DeeplClientResponseException extends RuntimeException {

    public DeeplClientResponseException() {
    }

    public DeeplClientResponseException(String message) {
        super(message);
    }
}
