package dev.jlynx.langcontrol.spacedrepetition;

public enum SpacedRepetitionSortBy {

    INTERVAL("interval", "currentInterval"),
    CREATED_AT("created", "createdAt"),
    NEXT_VIEW("view", "nextView");

    private final String urlValue;
    private final String fieldName;

    SpacedRepetitionSortBy(String urlValue, String fieldName) {
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
