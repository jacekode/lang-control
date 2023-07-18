package langcontrol.app.deck;

import java.util.Optional;

public enum LanguageCode {

    ENGLISH("en", "English"), GERMAN("de", "German"), SPANISH("es", "Spanish"),
    ITALIAN("it", "Italian"), FRENCH("fr", "French"), DUTCH("nl", "Dutch"),
    CZECH("cs", "Czech"), UKRAINIAN("uk", "Ukrainian"), PORTUGUESE("pt", "Portuguese"),
    DANISH("da", "Danish"), SWEDISH("sv", "Swedish"), POLISH("pl", "Polish"),
    LATVIAN("lv", "Latvian"), LITHUANIAN("lt", "Lithuanian"), SLOVAK("sk", "Slovak"),
    ESTONIAN("et",  "Estonian"), FINNISH("fi", "Finnish"), GREEK("el", "Greek"),
    SLOVENIAN("sl", "Slovenian"), BULGARIAN("bg", "Bulgarian"), ROMANIAN("ro", "Romanian"),
    TURKISH("tr", "Turkish"), HUNGARIAN("hu", "Hungarian"), INDONESIAN("id", "Indonesian");

    private final String code;
    private final String fullLanguageName;

    LanguageCode(String code, String fullLanguageName) {
        this.code = code;
        this.fullLanguageName = fullLanguageName;
    }

    public String getCode() {
        return code;
    }

    public String getFullLanguageName() {
        return fullLanguageName;
    }

    public static Optional<LanguageCode> findByCode(String code) {
        for (LanguageCode lc : LanguageCode.values()) {
            if (lc.code.equals(code)) {
                return Optional.of(lc);
            }
        }
        return Optional.empty();
    }
}
