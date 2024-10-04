package dev.jlynx.langcontrol.generator.openai;

public enum OpenAiCustomErrorCode {

    SAME_LANGUAGES("error1467");

    private final String value;

    OpenAiCustomErrorCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
