package dev.jlynx.langcontrol.flashcard;

import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Represents the grammatical part of speech of a word.
 */
public enum PartOfSpeech {

    NOUN("noun"), VERB("verb"), ADJECTIVE("adjective"),
    ADVERB("adverb"), PHRASE("phrase"), OTHER("other");

    private static final Logger LOG = LoggerFactory.getLogger(PartOfSpeech.class);

    private final String stringValue;

    PartOfSpeech(String stringValue) {
        this.stringValue = stringValue;
    }

    @JsonValue
    public String getStringValue() {
        return this.stringValue;
    }

    public static Optional<PartOfSpeech> findByStringValue(String value) {
        for (PartOfSpeech pos : PartOfSpeech.values()) {
            if (pos.stringValue.equals(value)) {
                return Optional.of(pos);
            }
        }
        LOG.trace("PartOfSpeech enum with the stringValue '{}' was not found", value);
        return Optional.empty();
    }
}
