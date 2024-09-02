package langcontrol.app.flashcard;

import java.util.Optional;

/**
 * Represents the part of speech of a single word in terms of grammar.
 */
public enum PartOfSpeech {

    NOUN("noun"), VERB("verb"), ADJECTIVE("adjective"),
    ADVERB("adverb"), PHRASE("phrase"), OTHER("word");

    private final String stringValue;

    PartOfSpeech(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public static Optional<PartOfSpeech> findByStringValue(String value) {
        for (PartOfSpeech pos : PartOfSpeech.values()) {
            if (pos.stringValue.equals(value)) {
                return Optional.of(pos);
            }
        }
        return Optional.empty();
    }
}
