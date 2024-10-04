package dev.jlynx.langcontrol.generator.openai.dto;

public record OpenaiErrorResponse(ErrorObject error) {

    public record ErrorObject(String message, String type, String param, String code) {
    }
}
