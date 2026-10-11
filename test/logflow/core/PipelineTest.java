package logflow.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Pipeline ortasına konan bir aşama: sıfır, bir veya çok çıktı verebilir. */
class PipelineTest {

    @Test
    void stageInTheMiddle_canEmitZeroOneOrManyOutputs() {
        Source<String> source = out -> {
            out.emit("a");
            out.emit("skip");
            out.emit("b");
        };
        Stage<String, String> stage = (in, out) -> {
            if (in.equals("skip")) {
                return;          // 0 çıktı
            }
            out.emit(in);        // 2 çıktı
            out.emit(in + in);
        };
        List<String> got = new ArrayList<>();

        Pipeline.from(source).then(stage).to(got::add).run();

        assertEquals(List.of("a", "aa", "b", "bb"), got);
    }

    @Test
    void lifecycleHooks_areCalledAroundRun() {
        List<String> events = new ArrayList<>();
        Stage<String, String> stage = new Stage<>() {
            @Override public void process(String in, Emitter<String> out) { out.emit(in); }
            @Override public void open() { events.add("open"); }
            @Override public void close() { events.add("close"); }
        };

        Pipeline.from((Source<String>) out -> out.emit("x")).then(stage).to(events::add).run();

        assertEquals(List.of("open", "x", "close"), events);
    }
}
