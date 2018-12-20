package pl.edu.wat.gadugadu.common;


import javafx.scene.paint.Color;

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
    public static final String[] statusNames ={
            "Available","Busy","Do not disturb","Away","Be right back","Offline"
    };

    public static final Color[] statusColors = {
            Color.web("#00DC00"),
            Color.web("#ffff00"),
            Color.web("#ff7b00"),
            Color.web("#00a0ff"),
            Color.web("#0000ff"),
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
