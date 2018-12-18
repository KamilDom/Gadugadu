package pl.edu.wat.gadugadu.common;

public enum AuthenticationStatus {
    SUCCESSFUL(0),
    ERROR(1);

    private final int value;

    AuthenticationStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static AuthenticationStatus valueOf(int value) {
        for (AuthenticationStatus s: values()) {
            if (s.value == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("invalid status: " + value);
    }
}
