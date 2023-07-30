package am.shoppingCommon.shoppingApplication.exception;

public class CategoryDoesNotExistsException extends RuntimeException {
    public CategoryDoesNotExistsException() {
    }

    public CategoryDoesNotExistsException(String message) {
        super(message);
    }

    public CategoryDoesNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryDoesNotExistsException(Throwable cause) {
        super(cause);
    }

    public CategoryDoesNotExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
