package logflow.model;

import java.time.Instant;
import java.util.Map;

/**
 * Pipeline'ın taşıdığı alan kaydı (domain record). Değişmezdir (immutable).
 *
 * <p>{@code attributes}: genişleme noktası. Sonraki aşamalar (zenginleştirme vb.)
 * bugünkü ayrıştırıcının bilmediği verileri kayda ekleyecek. Kayıt biçimi bu yüzden
 * alan eklemeden genişleyebilmeli. Harita savunmacı olarak kopyalanır ve değiştirilemez.
 */
public record LogRecord(
        Instant timestamp,
        String clientIp,
        String method,
        String path,
        int status,
        long bytes,
        String userAgent,
        Map<String, String> attributes,
        String raw) {

    public LogRecord {
        attributes = (attributes == null) ? Map.of() : Map.copyOf(attributes);
    }
}
