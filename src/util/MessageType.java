package util;

public enum MessageType {
	Text(0), Image(1), Emoji(2), File(3), Audio(4);
    private final int value;
    private MessageType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}