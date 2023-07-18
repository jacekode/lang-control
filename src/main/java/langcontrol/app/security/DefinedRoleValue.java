package langcontrol.app.security;

public enum DefinedRoleValue {

    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String value;

    DefinedRoleValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
