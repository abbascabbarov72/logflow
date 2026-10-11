package logflow.core;

/**
 * Pipeline'ın işlem adımı.
 *
 * process() değer döndürmez, Emitter'a iter: bir filtre girdi başına
 * sıfır, bir veya birden fazla çıktı üretebilir. Dönüş değeri 1:1 eşlemeyi zorlardı.
 */
public interface Stage<I, O> {
    void process(I input, Emitter<O> out) throws StageException;

    /** Yaşam döngüsü kancası (bu hafta kullanılmıyor). */
    default void open() {}

    default void close() {}
}
