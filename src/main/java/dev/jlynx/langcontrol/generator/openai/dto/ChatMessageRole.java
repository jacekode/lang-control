package dev.jlynx.langcontrol.generator.openai.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatMessageRole {

    SYSTEM, USER, ASSISTANT;

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }
}
