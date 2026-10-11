package logflow.core;

/** Pipeline çalışırken bir Stage'in fırlattığı StageException'ı sarar. */
public class PipelineException extends RuntimeException {
    public PipelineException(String message, Throwable cause) {
        super(message, cause);
    }
}
