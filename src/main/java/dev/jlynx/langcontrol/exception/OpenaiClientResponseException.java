package dev.jlynx.langcontrol.exception;

import dev.jlynx.langcontrol.generator.openai.OpenaiClient;

/**
 * Thrown when the {@link OpenaiClient} receives a 4xx or 5xx status code as a response.
 */
public class OpenaiClientResponseException extends RuntimeException {

    public OpenaiClientResponseException() {
    }

    public OpenaiClientResponseException(String message) {
        super(message);
    }
}
