package logflow.testing;

import logflow.core.Emitter;

import java.util.ArrayList;
import java.util.List;

/**
 * Test dublörü: emit edilen her şeyi bir listede toplar.
 * Aşama testleri dosya sistemine dokunmaz: aşama oluşturulur, bir dizgi verilir,
 * çıktılar bu emitter ile yakalanır. Her hafta yeniden kullanılacak.
 */
public final class CollectingEmitter<T> implements Emitter<T> {

    private final List<T> items = new ArrayList<>();

    @Override
    public void emit(T item) {
        items.add(item);
    }

    public List<T> items() {
        return List.copyOf(items);
    }

    public int size() {
        return items.size();
    }
}
