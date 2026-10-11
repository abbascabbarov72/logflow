# LogFlow — Increment 1: The Skeleton Pipeline

Bir log dosyasını **pipeline** üzerinden okuyup konsola yazdıran minimal bir iskelet.
Amaç, proje boyunca kullanılacak kavramları (Record, Stage, Source, Sink, Pipeline) oturtmaktır.

## Gereksinimler
- Java 17+ (JDK)

## Derleme ve Çalıştırma
```bash
./build.sh
./logflow data/access-small.log
```
Windows: `build.bat` ve `logflow.bat data\access-small.log`

Çıktı: dosyadaki her satır (≈200 satır) konsola yazılır.

## Yapı
```
src/main/java/logflow/
  Main.java                  argüman oku -> pipeline kur -> run()
  core/Stage.java            işlem adımı arayüzü
  core/Emitter.java          çıkış kanalı
  core/Source.java           kayıt üreten uç
  core/Sink.java             kayıt tüketen uç
  core/StageException.java   stage hata tipi
  core/Pipeline.java         Source -> Stage* -> Sink zinciri, run()
  io/FileLineSource.java     dosyadan satır okur
  io/ConsoleSink.java        konsola yazar
data/access-small.log        örnek log (200 satır)
```

## Tasarım Notu
`Stage.process` değer döndürmek yerine `Emitter` ile iter. Çünkü bir filtre girdi başına
sıfır, bir veya birden fazla çıktı üretebilir; dönüş değeri 1:1 eşlemeye zorlar.

## Teslim
Etiket: `v1`
