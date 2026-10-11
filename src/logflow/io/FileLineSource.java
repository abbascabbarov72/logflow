package logflow.io;

import logflow.core.Emitter;
import logflow.core.Source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Bir dosyayı okur, her satırı String olarak emit eder. */
public final class FileLineSource implements Source<String> {

    private final Path path;

    public FileLineSource(Path path) {
        this.path = path;
    }

    @Override
    public void produce(Emitter<String> out) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.emit(line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Dosya okunamadı: " + path, e);
        }
    }
}
