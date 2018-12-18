package pl.edu.wat.gadugadu.common;

public enum RegistrationStatus {
    NEW_USER(0),
    REGISTRATION_SUCCESSFUL(1),
    REGISTRATION_ERROR(2);

    private final int value;

    RegistrationStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static RegistrationStatus valueOf(int value) {
        for (RegistrationStatus s: values()) {
            if (s.value == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("invalid status: " + value);
    }
}
