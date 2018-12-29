package pl.edu.wat.gadugadu.common;

public enum ImageStatus {
    START(0),
    SENDING(1),
    STOP(2);

    private final int value;

    ImageStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static ImageStatus valueOf(int value) {
        for (ImageStatus s: values()) {
            if (s.value == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("invalid status: " + value);
    }
}
