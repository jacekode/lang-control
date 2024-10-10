package dev.jlynx.langcontrol.url;

public enum SortOrder {

    ASC, DESC;

    public String getUrlValue() {
        return this.name().toLowerCase();
    }

    public static SortOrder fromUrlValue(String urlValue) {
        for (SortOrder order : SortOrder.values()) {
            if (order.getUrlValue().equals(urlValue)) {
                return order;
            }
        }
        return null;
    }
}
