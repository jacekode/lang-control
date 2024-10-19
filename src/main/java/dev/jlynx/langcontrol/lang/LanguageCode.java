package dev.jlynx.langcontrol.lang;

import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Values of this enum represent particular world languages through both their full name in English and
 * their respective ISO compliant language codes.
 */
public enum LanguageCode {

    ENGLISH("en", "English"),
    GERMAN("de", "German"),
    SPANISH("es", "Spanish"),
    ITALIAN("it", "Italian"),
    FRENCH("fr", "French"),
    DUTCH("nl", "Dutch"),
    CZECH("cs", "Czech"),
    UKRAINIAN("uk", "Ukrainian"),
    PORTUGUESE("pt", "Portuguese"),
    DANISH("da", "Danish"),
    SWEDISH("sv", "Swedish"),
    POLISH("pl", "Polish"),
    LATVIAN("lv", "Latvian"),
    LITHUANIAN("lt", "Lithuanian"),
    SLOVAK("sk", "Slovak"),
    ESTONIAN("et",  "Estonian"),
    FINNISH("fi", "Finnish"),
    GREEK("el", "Greek"),
    SLOVENIAN("sl", "Slovenian"),
    BULGARIAN("bg", "Bulgarian"),
    ROMANIAN("ro", "Romanian"),
    TURKISH("tr", "Turkish"),
    HUNGARIAN("hu", "Hungarian"),
    INDONESIAN("id", "Indonesian");

    private static final Logger LOG = LoggerFactory.getLogger(LanguageCode.class);

    /**
     * The ISO 639-1 language code.
     */
    private final String code;

    /**
     * The name of the language in English.
     */
    private final String fullLanguageName;

    LanguageCode(String code, String fullLanguageName) {
        this.code = code;
        this.fullLanguageName = fullLanguageName;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getFullLanguageName() {
        return fullLanguageName;
    }

    /**
     * Searches for a {@code LanguageCode} enum with the given ISO 639-1 code.
     *
     * @param code ISO 639-1 language code.
     * @return an {@code Optional} with the found LanguageCode or {@code null} value if the code is unknown
     */
    public static Optional<LanguageCode> findByCode(String code) {
        for (LanguageCode lc : LanguageCode.values()) {
            if (lc.code.equals(code)) {
                return Optional.of(lc);
            }
        }
        LOG.trace("LanguageCode enum with the code '{}' was not found", code);
        return Optional.empty();
    }
}
