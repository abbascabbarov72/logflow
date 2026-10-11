package logflow.core;

/** Çıkış kanalı: bir Stage ürettiği kayıtları buraya iter. */
public interface Emitter<T> {
    void emit(T item);
}
