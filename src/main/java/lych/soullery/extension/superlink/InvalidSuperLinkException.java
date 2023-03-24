package lych.soullery.extension.superlink;

public class InvalidSuperLinkException extends Exception {
    private final Type type;

    public InvalidSuperLinkException(Type type) {
        super();
        this.type = type;
    }

    public InvalidSuperLinkException(String message, Type type) {
        super(message);
        this.type = type;
    }

    public InvalidSuperLinkException(String message, Throwable cause, Type type) {
        super(message, cause);
        this.type = type;
    }

    public InvalidSuperLinkException(Throwable cause, Type type) {
        super(cause);
        this.type = type;
    }

    protected InvalidSuperLinkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Type type) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        FARAWAY,
        NOT_IN_SAME_DIM,
        INVALID
    }
}
