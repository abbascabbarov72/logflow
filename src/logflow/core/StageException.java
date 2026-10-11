package logflow.core;

/** Bir Stage kaydı işlerken oluşan hata. */
public class StageException extends Exception {
    public StageException(String message) {
        super(message);
    }

    public StageException(String message, Throwable cause) {
        super(message, cause);
    }
}
