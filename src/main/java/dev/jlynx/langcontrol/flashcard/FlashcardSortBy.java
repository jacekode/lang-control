package dev.jlynx.langcontrol.flashcard;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FlashcardSortBy {

    INTERVAL("interval", "currentInterval"),
    TARGET_WORD("target", "targetWord"),
    CREATED_AT("created", "createdAt");

    private final String urlValue;
    private final String fieldName;

    FlashcardSortBy(String urlValue, String fieldName) {
        this.urlValue = urlValue;
        this.fieldName = fieldName;
    }

    public String getUrlValue() {
        return urlValue;
    }

    public String getFieldName() {
        return fieldName;
    }
}
