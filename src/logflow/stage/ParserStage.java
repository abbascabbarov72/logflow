package logflow.stage;

import logflow.core.Emitter;
import logflow.core.Stage;
import logflow.core.StageException;
import logflow.model.LogRecord;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ortak Günlük Biçimi (CLF) satırını LogRecord'a çevirir.
 * "Combined" biçimindeki referer ve user-agent alanlarını da destekler.
 *
 * <p>Hatalı satırlar BU HAFTA yalnızca atlanır ve sayılır. Doğru ele alma
 * (hata kuyruğu, ölü mektup vb.) 5. haftada eklenecek.
 */
public final class ParserStage implements Stage<String, LogRecord> {

    // host ident authuser [tarih] "istek" durum bayt ["referer" "user-agent"]
    private static final Pattern CLF = Pattern.compile(
            "^(\\S+)\\s+\\S+\\s+\\S+\\s+\\[([^\\]]+)\\]\\s+\"([^\"]*)\"\\s+(\\d{3})\\s+(\\d+|-)"
                    + "(?:\\s+\"([^\"]*)\"\\s+\"([^\"]*)\")?$");

    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    private long skipped;

    @Override
    public void process(String input, Emitter<LogRecord> out) throws StageException {
        Optional<LogRecord> record = parse(input);
        if (record.isPresent()) {
            out.emit(record.get());
        } else {
            skipped++;
        }
    }

    /** Şu ana kadar atlanan (hatalı veya boş) satır sayısı. */
    public long skippedCount() {
        return skipped;
    }

    private static Optional<LogRecord> parse(String input) {
        if (input == null) {
            return Optional.empty();
        }
        String line = input.trim();
        if (line.isEmpty()) {
            return Optional.empty();
        }

        Matcher m = CLF.matcher(line);
        if (!m.matches()) {
            return Optional.empty();
        }

        try {
            Instant timestamp = OffsetDateTime.parse(m.group(2), TIMESTAMP).toInstant();

            String[] request = m.group(3).trim().split("\\s+");
            if (request.length < 2) {
                return Optional.empty();
            }

            int status = Integer.parseInt(m.group(4));
            if (status < 100 || status > 599) {
                return Optional.empty();
            }

            String bytesText = m.group(5);
            long bytes = bytesText.equals("-") ? 0L : Long.parseLong(bytesText);

            String userAgent = (m.group(7) == null) ? "" : m.group(7);

            return Optional.of(new LogRecord(
                    timestamp, m.group(1), request[0], request[1],
                    status, bytes, userAgent, Map.of(), line));
        } catch (DateTimeParseException | NumberFormatException e) {
            return Optional.empty();
        }
    }
}
