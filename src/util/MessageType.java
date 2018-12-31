package util;

public enum MessageType {
	Text(0), Image(1), File(2), Audio(3);
    private final int value;
    private MessageType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}