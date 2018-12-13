package pl.edu.wat.gadugadu.common;

public enum PayloadType {
    AUTHENTICATION(0),
    STATUS_UPDATE(1),
    ONLINE_USERS_LIST(2),
    MESSAGE(3),
    IMAGE(4);

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