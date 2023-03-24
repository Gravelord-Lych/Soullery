package lych.soullery.world.event.manager;

public class NotLoadedException extends Exception {
    public NotLoadedException() {
        super();
    }

    public NotLoadedException(String message) {
        super(message);
    }

    public NotLoadedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotLoadedException(Throwable cause) {
        super(cause);
    }

    protected NotLoadedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
