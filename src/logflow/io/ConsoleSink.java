package logflow.io;

import logflow.core.Sink;

/** Gelen kayıtları konsola yazdırır. */
public final class ConsoleSink implements Sink<String> {
    @Override
    public void consume(String item) {
        System.out.println(item);
    }
}
