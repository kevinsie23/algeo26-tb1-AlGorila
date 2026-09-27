package algeo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("\n=== MENU UTAMA ===");
                System.out.println("1. Sistem Persamaan Linier (SPL)");
                System.out.println("2. Determinan Matriks");
                System.out.println("3. Matriks Balikan (Invers)");
                System.out.println("4. Interpolasi Polinomial");
                System.out.println("5. Natural Cubic Spline Interpolation");
                System.out.println("6. Regresi Spline Kubik");
                System.out.println("7. Keluar");
                
                int pilihan = IOHandler.readInt(sc, "Pilih menu (1-7): ", 1, 7);
                
                if (pilihan == 7) {
                    System.out.println("Terima kasih.");
                    break;
                }
                
                if (pilihan == 4 || pilihan == 5 || pilihan == 6) {
                    System.out.println("Fitur belum tersedia");
                    continue;
                }
                
                if (pilihan == 1) {
                    menuSPL(sc);
                } else if (pilihan == 2) {
                    menuDeterminan(sc);
                } else if (pilihan == 3) {
                    menuInvers(sc);
                }
            } catch (Exception e) {
                boolean hasNext = false;
                try {
                    hasNext = sc.hasNextLine();
                } catch (Exception ex) {
                    hasNext = false;
                }
                if (!hasNext) {
                    System.out.println("Input habis, program dihentikan.");
                    break;
                } else {
                    System.out.println("Terjadi kesalahan: " + e.getMessage());
                }
            }
        }
    }

    private static void menuSPL(Scanner sc) {
        System.out.println("\n--- Sistem Persamaan Linier (SPL) ---");
        System.out.println("1. Metode Eliminasi Gauss");
        System.out.println("2. Metode Eliminasi Gauss-Jordan");
        System.out.println("3. Metode Matriks Balikan");
        System.out.println("4. Kaidah Cramer");
        int metode = IOHandler.readInt(sc, "Pilih metode (1-4): ", 1, 4);
        
        String namaValidasi = (metode == 4) ? "Kaidah Cramer" : null;
        Matrix m = bacaMatriks(sc, true, namaValidasi);
        if (m == null) return;
        
        SPLResult res = null;
        try {
            if (metode == 1) res = SPL.gauss(m);
            else if (metode == 2) res = SPL.gaussJordan(m);
            else if (metode == 3) res = SPL.inverseMethod(m);
            else if (metode == 4) res = SPL.cramer(m);
        } catch (Exception e) {
            System.out.println("Error saat menghitung SPL: " + e.getMessage());
            return;
        }
        
        System.out.println("\n--- Hasil SPL ---");
        if (res.langkah != null && !res.langkah.isEmpty()) {
            System.out.println("Langkah-langkah:");
            for (String step : res.langkah) {
                System.out.println(step);
            }
        }
        
        String hasilDisplay = res.toDisplayString(m.getCols() - 1);
        System.out.println("\nSolusi:");
        System.out.println(hasilDisplay);
        
        tanyaSimpan(sc, res.namaMetode, m, hasilDisplay);
    }

    private static void menuDeterminan(Scanner sc) {
        System.out.println("\n--- Determinan Matriks ---");
        System.out.println("1. Metode Reduksi Baris");
        System.out.println("2. Metode Ekspansi Kofaktor");
        int metode = IOHandler.readInt(sc, "Pilih metode (1-2): ", 1, 2);
        
        Matrix m = bacaMatriks(sc, false, null);
        if (m == null) return;
        
        if (m.getRows() != m.getCols()) {
            System.out.println("matriks tidak memiliki determinan");
            return;
        }
        
        double det = 0;
        try {
            if (metode == 1) det = Determinan.rowReduction(m);
            else det = Determinan.cofactorExpansion(m);
        } catch (Exception e) {
            System.out.println("Error saat menghitung determinan: " + e.getMessage());
            return;
        }
        
        String namaMetode = metode == 1 ? "Determinan (Reduksi Baris)" : "Determinan (Ekspansi Kofaktor)";
        String hasilDisplay = "Determinan = " + IOHandler.formatNumber(det);
        System.out.println("\n" + hasilDisplay);
        
        tanyaSimpan(sc, namaMetode, m, hasilDisplay);
    }

    private static Matrix hitungInversSementara(Matrix m) {
        // SEMENTARA: menggunakan SPL.rref + cek determinan sendiri, karena
        // Invers.augmentInverse milik Kevin belum selesai (bagian OBE Gauss-Jordan kosong).
        // Ganti kembali setelah Kevin memperbaikinya.
        try {
            double det = Determinan.rowReduction(m);
            if (Math.abs(det) < 1e-9) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        
        int n = m.getRows();
        Matrix augmented = m.augment(Matrix.createIdentityMatrix(n));
        Matrix rrefResult = SPL.rref(augmented, n, null);
        
        Matrix inv = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inv.setElmt(i, j, rrefResult.getElmt(i, j + n));
            }
        }
        return inv;
    }

    private static void menuInvers(Scanner sc) {
        System.out.println("\n--- Matriks Balikan (Invers) ---");
        System.out.println("1. Metode Augmentasi");
        System.out.println("2. Metode Adjoin");
        int metode = IOHandler.readInt(sc, "Pilih metode (1-2): ", 1, 2);
        
        String namaValidasi = (metode == 2) ? "Adjoin" : null;
        Matrix m = bacaMatriks(sc, false, namaValidasi);
        if (m == null) return;
        
        if (m.getRows() != m.getCols()) {
            System.out.println("matriks tidak memiliki balikan");
            return;
        }
        
        Matrix inv = null;
        try {
            if (metode == 1) inv = hitungInversSementara(m);
            else inv = Invers.adjoinInverse(m);
        } catch (Exception e) {
            System.out.println("Error saat menghitung invers: " + e.getMessage());
            return;
        }
        
        if (inv == null) {
            System.out.println("matriks tidak memiliki balikan");
            return;
        }
        
        String namaMetode = metode == 1 ? "Invers (Augmentasi)" : "Invers (Adjoin)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inv.getRows(); i++) {
            for (int j = 0; j < inv.getCols(); j++) {
                sb.append(IOHandler.formatNumber(inv.getElmt(i, j)));
                if (j < inv.getCols() - 1) sb.append(" ");
            }
            sb.append("\n");
        }
        String hasilDisplay = sb.toString().trim();
        
        System.out.println("\nSolusi Invers:\n" + hasilDisplay);
        
        tanyaSimpan(sc, namaMetode, m, hasilDisplay);
    }

    private static Matrix bacaMatriks(Scanner sc, boolean augmented, String namaMetodeUntukValidasi) {
        System.out.println("Sumber input:");
        System.out.println("1. Manual (Keyboard)");
        System.out.println("2. File (.txt)");
        int tipe = IOHandler.readInt(sc, "Pilih (1-2): ", 1, 2);
        
        Matrix res = null;
        if (tipe == 1) {
            int maxRows = 11;
            int maxCols = augmented ? 12 : 11;
            int rows = IOHandler.readInt(sc, "Jumlah baris: ", 1, maxRows);
            int cols = IOHandler.readInt(sc, "Jumlah kolom: ", 1, maxCols);
            System.out.println("Masukkan isi matriks:");
            try {
                res = IOHandler.readMatrixKeyboard(sc, rows, cols);
            } catch (Exception e) {
                System.out.println("Gagal membaca matriks manual: " + e.getMessage());
                return null;
            }
        } else {
            System.out.print("Masukkan path/nama file: ");
            String path = sc.nextLine().trim();
            try {
                res = IOHandler.readMatrixFile(path, 1001, 1002);
            } catch (FileNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException | IOException e) {
                System.out.println("Error format file: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        
        if (res != null && namaMetodeUntukValidasi != null) {
            String namaMetodeUpper = namaMetodeUntukValidasi.toUpperCase();
            if (namaMetodeUpper.contains("CRAMER") || namaMetodeUpper.contains("ADJOIN")) {
                int limitCols = namaMetodeUpper.contains("CRAMER") ? res.getCols() - 1 : res.getCols();
                if (limitCols > 12) {
                    System.out.println("Metode ini menggunakan ekspansi kofaktor yang sangat lambat untuk matriks besar (>12x12). Silakan pilih metode lain atau gunakan matriks yang lebih kecil.");
                    return null;
                }
            }
        }
        return res;
    }

    private static void tanyaSimpan(Scanner sc, String namaMetode, Matrix inputM, String hasil) {
        int simpan = IOHandler.readInt(sc, "Simpan ke file? (1. Ya, 2. Tidak): ", 1, 2);
        if (simpan == 1) {
            System.out.print("Masukkan nama file keluaran (misal output.txt): ");
            String path = sc.nextLine().trim();
            try (PrintWriter pw = new PrintWriter(path)) {
                pw.println("Metode: " + namaMetode);
                pw.println("\nMatriks Input:");
                for (int i = 0; i < inputM.getRows(); i++) {
                    for (int j = 0; j < inputM.getCols(); j++) {
                        pw.print(IOHandler.formatNumber(inputM.getElmt(i, j)));
                        if (j < inputM.getCols() - 1) pw.print(" ");
                    }
                    pw.println();
                }
                pw.println("\nHasil:");
                pw.println(hasil);
                System.out.println("Berhasil disimpan ke " + path);
            } catch (Exception e) {
                System.out.println("Gagal menyimpan ke file: " + e.getMessage());
            }
        }
    }
}