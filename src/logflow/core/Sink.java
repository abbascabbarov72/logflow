package logflow.core;

/** Kayıtları tüketen son uç. */
public interface Sink<I> {
    void consume(I item);
}
