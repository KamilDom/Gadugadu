package pl.edu.wat.gadugadu.common;

public enum PayloadType {
    AUTHENTICATION(0),
    NEW_CLIENT_CONNECTED(1),
    STATUS_UPDATE(2),
    ONLINE_USERS_LIST(3),
    MESSAGE(4),
    IMAGE(5);

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