# Aljabar Linier dan Geometri Tubes 1 Template
## Deskripsi Program
(JANGAN LUPA ISI DESKRIPSI PROGRAM, CEK PALING BAWAH JUGA)

## Requirements

- Java 17 atau lebih baru
- Maven 3.6.3 atau lebih baru

Periksa instalasi dengan perintah berikut.

```bash
java --version
mvn --version
```

## Struktur direktori

```text
.
├── bin
├── docs
├── image
├── src
│   └── main
│       └── java
│           └── algeo
│               ├── modules
│               │   ├── Determinan.java
│               │   ├── ImageInpainting.java
│               │   ├── InterpolasiPolinomial.java
│               │   ├── Invers.java
│               │   ├── IOHandler.java
│               │   ├── Matrix.java
│               │   ├── RegresiSpline.java
│               │   ├── SPL.java
│               │   ├── SplineKubik.java
│               │   └── SPLResult.java
│               └── App.java
├── test
├── pom.xml
└── README.md
```

Kode program diletakkan di dalam `src/main/java/algeo`. Kelas utama program adalah `algeo.App`.

- `bin`: berkas hasil kompilasi atau JAR final
- `docs`: laporan tugas besar
- `src`: kode sumber program
- `test`: berkas kasus uji

## Menjalankan program

Kompilasi proyek:

```bash
mvn clean compile
```

Jalankan program CLI:

```bash
mvn exec:java
```

Buat berkas JAR:

```bash
mvn clean package
```

Berkas JAR akan tersedia di dalam direktori `target`.

Untuk menggunakan JavaFX, sesuaikan kelas `App.java`, kemudian jalankan:

```bash
mvn clean javafx:run
```

## Alur Penggunaan
(JANGAN LUPAA ALUR PENGGUNAAN)
(SAMA ATUR" MENAJLANKAN PROGRAM, KALO DI ATAS ADA YG BEDA OR KURANG LENGKAP GANTI AJA, SOALNYA TEMPLATE DARI ASISTEN)