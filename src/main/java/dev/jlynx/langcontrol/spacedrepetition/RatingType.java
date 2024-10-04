package dev.jlynx.langcontrol.spacedrepetition;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RatingType {
    LEARN_KNOW("know"), LEARN_DONT_KNOW("dknow"),
    REVIEW_REMEMBER("remember"), REVIEW_PARTIALLY("partially"), REVIEW_FORGOT("forgot");

    private String urlValue;

    RatingType(String urlValue) {
        this.urlValue = urlValue;
    }

    @JsonValue
    public String getUrlValue() {
        return urlValue;
    }
}
