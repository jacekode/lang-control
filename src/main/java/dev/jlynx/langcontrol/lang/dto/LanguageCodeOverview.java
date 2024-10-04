package dev.jlynx.langcontrol.lang.dto;

import dev.jlynx.langcontrol.lang.LanguageCode;

public record LanguageCodeOverview(String language, String code) {

    public static LanguageCodeOverview fromLanguageCode(LanguageCode langCode) {
        return new LanguageCodeOverview(langCode.getFullLanguageName(), langCode.getCode());
    }
}
