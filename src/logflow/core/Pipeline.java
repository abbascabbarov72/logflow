package logflow.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Source -> Stage* -> Sink zincirini kurar ve çalıştırır.
 * Tip güvenliği builder (from/then/to) üzerinden sağlanır.
 */
public final class Pipeline {

    private final Source<Object> source;
    private final List<Stage<Object, Object>> stages;
    private final Sink<Object> sink;

    @SuppressWarnings("unchecked")
    private Pipeline(Source<?> source, List<Stage<?, ?>> stages, Sink<?> sink) {
        this.source = (Source<Object>) source;
        this.sink = (Sink<Object>) sink;
        List<Stage<Object, Object>> copy = new ArrayList<>();
        for (Stage<?, ?> s : stages) {
            copy.add((Stage<Object, Object>) s);
        }
        this.stages = List.copyOf(copy);
    }

    public static <T> Builder<T> from(Source<T> source) {
        return new Builder<>(source, new ArrayList<>());
    }

    /** Kayıtları kaynaktan sink'e iter. */
    public void run() {
        for (Stage<Object, Object> s : stages) {
            s.open();
        }
        try {
            // Zinciri sondan başa doğru kur.
            Emitter<Object> next = sink::consume;
            for (int i = stages.size() - 1; i >= 0; i--) {
                final Stage<Object, Object> stage = stages.get(i);
                final Emitter<Object> downstream = next;
                next = item -> {
                    try {
                        stage.process(item, downstream);
                    } catch (StageException e) {
                        throw new PipelineException(
                                "Stage hatası: " + stage.getClass().getSimpleName(), e);
                    }
                };
            }
            source.produce(next);
        } finally {
            for (int i = stages.size() - 1; i >= 0; i--) {
                stages.get(i).close();
            }
        }
    }

    public static final class Builder<T> {
        private final Source<?> source;
        private final List<Stage<?, ?>> stages;

        private Builder(Source<?> source, List<Stage<?, ?>> stages) {
            this.source = source;
            this.stages = stages;
        }

        public <R> Builder<R> then(Stage<? super T, R> stage) {
            List<Stage<?, ?>> next = new ArrayList<>(stages);
            next.add(stage);
            return new Builder<>(source, next);
        }

        public Pipeline to(Sink<? super T> sink) {
            return new Pipeline(source, stages, sink);
        }
    }
}
