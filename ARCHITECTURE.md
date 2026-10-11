# ARCHITECTURE — Increment 1

## İki kutulu diyagram

```
+--------------------+        Emitter<String>        +------------------+
|   FileLineSource   | -----------------------------> |   ConsoleSink    |
|  (Source<String>)  |   (Pipeline tarafından bağlanır) | (Sink<String>)   |
+--------------------+                                 +------------------+
          \                                                   /
           \________________  Pipeline  ____________________/
                     (ordered list of Stages — şimdilik boş)
```

## Bileşenler ve konnektörler
| Bileşen | Rol |
|---|---|
| `FileLineSource` | Dosyadan satır üretir (Source) |
| `ConsoleSink` | Satırları yazdırır (Sink) |
| `Pipeline` | Bileşenleri bağlar, `run()` ile kayıtları iter |
| `Main` | Yalnızca kurulum ve `run()` çağrısı |

Konnektör: `Emitter<T>`. Her bileşen bir sonrakini yalnızca bu arayüz üzerinden tanır.

## Arayüz vs. gerçekleme
`Source`, `Sink`, `Stage`, `Emitter` arayüzleri sözleşmedir. `FileLineSource` ve
`ConsoleSink` değiştirilebilir gerçeklemelerdir; Pipeline onları somut sınıf olarak bilmez.
Bu sayede ileride (ağ kaynağı, dosya sink'i, filtre/parse stage'leri) mevcut kod değişmeden eklenebilir.

## Kararlar
- **Push modeli**: Source kayıtları Emitter zinciriyle iter.
- **Emitter ile çıktı**: 0..N çıktı mümkün (filtre, ayrıştırma).
- **Main'de iş mantığı yok**: yalnızca argüman → kurulum → `run()`.
- **Yaşam döngüsü**: `open()/close()` kancaları var, bu hafta kullanılmıyor; Pipeline yine de çağırır.
- **Hata**: `StageException` Pipeline'da `PipelineException`'a sarılır.
