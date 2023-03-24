package lych.soullery.util;

public class EnumConstantNotFoundException extends Exception {
    private final int id;

    public EnumConstantNotFoundException(int id) {
        this.id = id;
    }

    public EnumConstantNotFoundException(int id, Throwable cause) {
        super(cause);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
