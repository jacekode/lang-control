package dev.jlynx.langcontrol.url;

public enum SortOrder {

    ASC, DESC;

    public String getUrlValue() {
        return this.name().toLowerCase();
    }
}
