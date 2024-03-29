package pl.edu.wat.gadugadu.common;

public enum PayloadType {
    REGISTRATION(0),
    AUTHENTICATION(1),
    NEW_CLIENT_CONNECTED(2),
    STATUS_UPDATE(3),
    ONLINE_USERS_LIST(4),
    MESSAGE(5),
    IMAGE(6);

    private final int value;

    PayloadType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static PayloadType valueOf(int value) {
        for (PayloadType s: values()) {
            if (s.value == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("invalid status: " + value);
    }

};