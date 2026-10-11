package logflow.stage;

import logflow.model.LogRecord;
import logflow.testing.CollectingEmitter;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** ParserStage testleri: dosya yok, yalnızca dizgi girer, kayıt çıkar. */
class ParserStageTest {

    private static final String VALID =
            "192.168.1.10 - - [11/Oct/2026:10:00:00 +0300] \"GET /index.html HTTP/1.1\" 200 1234 \"-\" \"Mozilla/5.0\"";

    private final ParserStage parser = new ParserStage();
    private final CollectingEmitter<LogRecord> out = new CollectingEmitter<>();

    private void feed(String... lines) throws Exception {
        for (String line : lines) {
            parser.process(line, out);
        }
    }

    @Test
    void validLine_isParsedIntoRecord() throws Exception {
        feed(VALID);

        assertEquals(1, out.size());
        LogRecord r = out.items().get(0);
        assertEquals(Instant.parse("2026-10-11T07:00:00Z"), r.timestamp());
        assertEquals("192.168.1.10", r.clientIp());
        assertEquals("GET", r.method());
        assertEquals("/index.html", r.path());
        assertEquals(200, r.status());
        assertEquals(1234L, r.bytes());
        assertEquals("Mozilla/5.0", r.userAgent());
        assertTrue(r.attributes().isEmpty());
        assertEquals(VALID, r.raw());
        assertEquals(0L, parser.skippedCount());
    }

    @Test
    void missingField_isSkippedAndCounted() throws Exception {
        feed("192.168.1.10 - - [11/Oct/2026:10:00:00 +0300] \"GET /index.html HTTP/1.1\" 200");

        assertEquals(0, out.size());
        assertEquals(1L, parser.skippedCount());
    }

    @Test
    void badTimestamp_isSkippedAndCounted() throws Exception {
        feed("192.168.1.10 - - [99/Foo/2026:10:00:00 +0300] \"GET / HTTP/1.1\" 200 10 \"-\" \"curl\"");

        assertEquals(0, out.size());
        assertEquals(1L, parser.skippedCount());
    }

    @Test
    void badStatusCode_isSkippedAndCounted() throws Exception {
        feed("10.0.0.5 - - [11/Oct/2026:10:00:00 +0300] \"GET / HTTP/1.1\" abc 10 \"-\" \"curl\"",
             "10.0.0.5 - - [11/Oct/2026:10:00:00 +0300] \"GET / HTTP/1.1\" 999 10 \"-\" \"curl\"");

        assertEquals(0, out.size());
        assertEquals(2L, parser.skippedCount());
    }

    @Test
    void emptyLine_isSkippedAndCounted() throws Exception {
        feed("", "   ");

        assertEquals(0, out.size());
        assertEquals(2L, parser.skippedCount());
    }

    @Test
    void extraWhitespace_isTolerated() throws Exception {
        feed("   10.0.0.5   -   -   [11/Oct/2026:10:00:00 +0300]   \"GET   /a   HTTP/1.1\"   404   55   \"-\"   \"curl\"   ");

        assertEquals(1, out.size());
        LogRecord r = out.items().get(0);
        assertEquals("10.0.0.5", r.clientIp());
        assertEquals("GET", r.method());
        assertEquals("/a", r.path());
        assertEquals(404, r.status());
        assertEquals(55L, r.bytes());
    }

    @Test
    void quotedUserAgentWithSpaces_isKeptWhole() throws Exception {
        feed("10.0.0.5 - - [11/Oct/2026:10:00:00 +0300] \"GET / HTTP/1.1\" 200 10 \"-\" "
                + "\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36\"");

        assertEquals(1, out.size());
        assertEquals("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
                out.items().get(0).userAgent());
    }

    @Test
    void queryString_staysInPath() throws Exception {
        feed("10.0.0.5 - - [11/Oct/2026:10:00:00 +0300] \"GET /search?q=log&page=2 HTTP/1.1\" 200 10 \"-\" \"curl\"");

        assertEquals(1, out.size());
        assertEquals("/search?q=log&page=2", out.items().get(0).path());
    }

    @Test
    void dashBytes_becomeZero() throws Exception {
        feed("10.0.0.5 - - [11/Oct/2026:10:00:00 +0300] \"GET / HTTP/1.1\" 304 - \"-\" \"curl\"");

        assertEquals(1, out.size());
        assertEquals(0L, out.items().get(0).bytes());
    }

    @Test
    void plainClfWithoutAgent_hasEmptyUserAgent() throws Exception {
        feed("10.0.0.5 - - [11/Oct/2026:10:00:00 +0300] \"GET / HTTP/1.1\" 200 10");

        assertEquals(1, out.size());
        assertEquals("", out.items().get(0).userAgent());
    }

    @Test
    void mixedInput_emitsValidOnesAndCountsBadOnes() throws Exception {
        feed(VALID, "garbage line", "", VALID);

        assertEquals(2, out.size());
        assertEquals(2L, parser.skippedCount());
    }
}
