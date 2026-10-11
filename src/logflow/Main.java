package logflow;

import logflow.core.Pipeline;
import logflow.core.PipelineException;
import logflow.io.ConsoleSink;
import logflow.io.FileLineSource;

import java.io.UncheckedIOException;
import java.nio.file.Path;

/** Yalnızca: argümanları oku, pipeline'ı kur, run() çağır. */
public final class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Kullanım: logflow <log-dosyası>");
            System.exit(2);
        }

        Pipeline pipeline = Pipeline
                .from(new FileLineSource(Path.of(args[0])))
                .to(new ConsoleSink());

        try {
            pipeline.run();
        } catch (UncheckedIOException | PipelineException e) {
            System.err.println("Hata: " + e.getMessage());
            System.exit(1);
        }
    }
}
