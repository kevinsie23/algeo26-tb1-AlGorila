# 🦍 Gorilla Calculator: Tugas Besar 1 IF2123

> Pustaka aljabar linier dalam Java yang ditulis dari nol, lengkap dengan antarmuka CLI dan GUI (JavaFX). 

Proyek ini merupakan pemenuhan Tugas Besar 1 IF2123 Aljabar Linier dan Geometri Semester I 2026/2027. Ruang lingkup tugas meliputi sistem persamaan linier, determinan, matriks balikan, interpolasi polinomial, interpolasi splina kubik natural, regresi splina kubik, serta fitur bonus pemulihan citra (*image inpainting*). Ketentuan lengkap mengikuti dokumen spesifikasi tugas besar.

Seluruh algoritma perhitungan matriks dikembangkan secara mandiri menggunakan struktur data `double[][]` tanpa menggunakan *library* eksternal seperti JAMA, EJML, atau Commons Math.

---

## Anggota Kelompok
**Kelompok AlGorila**

| NIM        | Nama |
| ---------- | ---------------------- |
| 13525002   | Ahmad Boutros Fathir   |
| 13525053   | Kevin Sie              |
| 13525062   | Rafel Dzinun Muhammad  |

---

## Fitur Utama
1. **Sistem Persamaan Linier (SPL)** (Gauss, Gauss-Jordan, Matriks Balikan, Cramer)
2. **Kalkulator Determinan** (OBE / Reduksi Baris, Ekspansi Kofaktor)
3. **Pencarian Invers Matriks** (Augmentasi, Adjoin)
4. **Interpolasi Polinomial**
5. **Natural Cubic Spline Interpolation**
6. **Regresi Spline Kubik**
7. **Image Inpainting (Bonus)** (Metode iteratif Gauss-Seidel berbasis diskritisasi Persamaan Laplace)
8. **Graphical User Interface (Bonus)** (GUI interaktif yang dibangun dengan JavaFX)

---

## Requirements
- **Java 17** atau lebih baru
- **Maven 3.6.3** atau lebih baru

Periksa instalasi dengan perintah berikut:
```bash
java --version
mvn --version
```
##  Struktur Direktori
```text
.
├── bin
├── docs
├── image
├── src
│   └── main
│       ├── resources  
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
│               ├── App.java
│               ├── AppGUI.java
│               └── GorillaController.java
├── test
├── pom.xml
└── README.md
```


---

##  Menjalankan Program

Pastikan Anda berada di direktori *root* proyek (sejajar dengan `pom.xml`).

**1. Kompilasi dan Build Proyek:**
```bash
mvn clean package
```

**2. Menjalankan Program CLI:**
```bash
java -jar bin/matrix-calculator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

**3. Menjalankan Program GUI (JavaFX):**
```bash
mvn clean javafx:run
```

*Berkas JAR akan tersedia di dalam direktori `bin`.*

---
