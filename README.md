# LogFlow — Artım 2: Tipli Kayıtlar ve İlk Gerçek Filtre

Pipeline artık `String` değil, değişmez bir alan modeli (`LogRecord`) taşır.
Access log satırları (CLF / Combined) ayrıştırılır, hatalı satırlar atlanır ve sayılır.

## Gereksinimler
- Java 17+ (JDK)

## Derleme ve Çalıştırma
```bash
./build.sh
./logflow data/access-small.log          # 200 kayıt, 0 hatalı satır
./logflow data/access-with-errors.log    # 20 kayıt, 4 hatalı satır
```
Windows: `build.bat` ve `logflow.bat data\access-small.log`

Örnek çıktı:
```
2026-10-11T07:00:00Z 192.0.2.99 GET / 200 16169B "Mozilla/5.0"
...
--- Özet: 200 kayıt yazdırıldı, 0 hatalı satır atlandı ---
```

## Yapı
```
src/logflow/
  Main.java                 argüman oku -> pipeline kur -> run() -> özet
  core/                     Stage, Emitter, Source, Sink, Pipeline, istisnalar (DEĞİŞMEDİ)
  model/LogRecord.java      YENİ  değişmez alan kaydı
  stage/ParserStage.java    YENİ  CLF ayrıştırıcı
  io/FileLineSource.java    (değişmedi)
  io/ConsoleSink.java       DEĞİŞTİ  LogRecord'u tek satır yazar
test/logflow/
  testing/CollectingEmitter.java   test dublörü (toplayıcı emitter)
  stage/ParserStageTest.java       11 test
  io/ConsoleSinkTest.java          2 test
  core/PipelineTest.java           2 test
data/access-small.log, data/access-with-errors.log
```

## Hatalı satırlar
Bu hafta yalnızca **atlanır ve sayılır** (boş satır, eksik alan, geçersiz zaman damgası,
geçersiz durum kodu). Doğru ele alma (hata kuyruğu, raporlama) **5. haftada** gelecek;
mimari katman katman inşa ediliyor.

## Testleri Çalıştırma
JUnit 5 gerekir. IntelliJ IDEA'da: `test` klasörüne sağ tık >
**Mark Directory as > Test Sources Root**; `@Test` kırmızıysa `Alt+Enter` >
**Add 'JUnit5.x' to classpath**. Sonra `test` klasörüne sağ tık > **Run 'All Tests'**.

Maven kuruluysa: `mvn test` (kapsama raporu: `mvn verify`, `target/site/jacoco/index.html`).

## Satır Kapsama (Line Coverage)

| Paket | Satır kapsaması |
|---|-----------------|
| `logflow.stage` (ParserStage) | **%93**         |
| Tüm proje | **%69**         |

## Teslim
Etiket: `v2` — değişiklik günlüğü için `CHANGELOG.md`.
