package pl.edu.wat.gadugadu.common;

public enum UserStatus {
    AVAILABLE(0),
    BUSY(1),
    DO_NOT_DISTURB(2),
    AWAY(3),
    BE_RIGHT_BACK(4),
    OFFLINE(5);

    private final int value;
    public static final String[] statusFromInputBox ={
            "/ava","/busy","/dnd","/away","/brb"
    };

    UserStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static UserStatus valueOf(int value) {
        for (UserStatus s: values()) {
            if (s.value == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("invalid status: " + value);
    }
}
