package logflow.io;

import logflow.core.Sink;
import logflow.model.LogRecord;

import java.io.PrintStream;

/** LogRecord'u okunabilir, tek satırlık biçimde yazdırır. */
public final class ConsoleSink implements Sink<LogRecord> {

    private final PrintStream out;
    private long count;

    public ConsoleSink() {
        this(System.out);
    }

    public ConsoleSink(PrintStream out) {
        this.out = out;
    }

    @Override
    public void consume(LogRecord item) {
        out.println(format(item));
        count++;
    }

    /** Yazdırılan kayıt sayısı. */
    public long count() {
        return count;
    }

    static String format(LogRecord r) {
        return r.timestamp() + " " + r.clientIp() + " " + r.method() + " " + r.path()
                + " " + r.status() + " " + r.bytes() + "B \"" + r.userAgent() + "\"";
    }
}
