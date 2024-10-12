package dev.jlynx.langcontrol.exception;

/**
 * Thrown when the requested data cannot be found.
 */
public class AssetNotFoundException extends RuntimeException {

    public AssetNotFoundException() {
    }

    public AssetNotFoundException(String message) {
        super(message);
    }
}
