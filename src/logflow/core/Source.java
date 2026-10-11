package logflow.core;

/** Kayıt üreten uç: tüm kayıtları verilen Emitter'a iter. */
public interface Source<O> {
    void produce(Emitter<O> out);
}
