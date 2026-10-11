package logflow.io;

import logflow.model.LogRecord;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsoleSinkTest {

    private static LogRecord sample() {
        return new LogRecord(Instant.parse("2026-10-11T07:00:00Z"), "192.0.2.99", "GET",
                "/index.html", 200, 1234L, "Mozilla/5.0", Map.of(), "raw");
    }

    @Test
    void format_isSingleReadableLine() {
        assertEquals("2026-10-11T07:00:00Z 192.0.2.99 GET /index.html 200 1234B \"Mozilla/5.0\"",
                ConsoleSink.format(sample()));
    }

    @Test
    void consume_printsOneLineAndCounts() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ConsoleSink sink = new ConsoleSink(new PrintStream(buffer, true));

        sink.consume(sample());

        assertEquals(1L, sink.count());
        assertEquals(ConsoleSink.format(sample()) + System.lineSeparator(), buffer.toString());
    }
}
