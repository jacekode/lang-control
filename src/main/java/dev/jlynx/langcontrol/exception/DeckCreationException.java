package dev.jlynx.langcontrol.exception;

import dev.jlynx.langcontrol.deck.Deck;

/**
 * Thrown when an error has occurred when creating a new {@link Deck}.
 */
public class DeckCreationException extends RuntimeException {

    public DeckCreationException() {
    }

    public DeckCreationException(String message) {
        super(message);
    }
}
