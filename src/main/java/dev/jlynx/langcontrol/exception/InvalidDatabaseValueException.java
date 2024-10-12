package dev.jlynx.langcontrol.exception;

import dev.jlynx.langcontrol.lang.LanguageCodeDatabaseConverter;

/**
 * Thrown when an error has occurred during a conversion between an enum and a database column value.
 *
 * @see LanguageCodeDatabaseConverter
 */
public class InvalidDatabaseValueException extends RuntimeException {

    public InvalidDatabaseValueException() {
    }

    public InvalidDatabaseValueException(String message) {
        super(message);
    }
}
