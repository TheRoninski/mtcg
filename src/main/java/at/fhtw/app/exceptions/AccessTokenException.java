package at.fhtw.app.exceptions;

public class AccessTokenException extends RuntimeException {
    private final boolean needsAdmin;

    public AccessTokenException(boolean needsAdmin) {
        super("Access token invalid or insufficient privileges");
        this.needsAdmin = needsAdmin;
    }

    public AccessTokenException(String message) {
        super(message);
        this.needsAdmin = false;
    }

    public boolean isNeedsAdmin() {
        return needsAdmin;
    }
}
