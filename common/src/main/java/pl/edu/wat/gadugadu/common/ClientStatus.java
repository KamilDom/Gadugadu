package pl.edu.wat.gadugadu.common;

public enum ClientStatus {
    AVAILABLE(0),
    BUSY(1),
    DO_NOT_DISTURB(2),
    AWAY(3),
    BE_RIGHT_BACK(4);

    private final int value;
    public static final String[] statusFromInputBox ={
            "/ava","/busy","/dnd","/away","/brb"
    };

    ClientStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static ClientStatus valueOf(int value) {
        for (ClientStatus s: values()) {
            if (s.value == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("invalid status: " + value);
    }
}
