package algeo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import algeo.modules.Determinan;
import algeo.modules.IOHandler;
import algeo.modules.InterpolasiPolinomial;
import algeo.modules.Invers;
import algeo.modules.Matrix;
import algeo.modules.RegresiSpline;
import algeo.modules.SPL;
import algeo.modules.SPLResult;
import algeo.modules.SplineKubik;
import algeo.modules.ImageInpainting;

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
                System.out.println("7. Image Inpainting");
                System.out.println("8. Keluar");
                
                int pilihan = IOHandler.readInt(sc, "Pilih menu (1-8): ", 1, 8);
                
                if (pilihan == 8) {
                    System.out.println("Terima kasih.");
                    break;
                }
                

                
                if (pilihan == 1) {
                    menuSPL(sc);
                } else if (pilihan == 2) {
                    menuDeterminan(sc);
                } else if (pilihan == 3) {
                    menuInvers(sc);
                } else if (pilihan == 4) {
                    menuInterpolasi(sc);
                } else if (pilihan == 5) {
                    menuSpline(sc);
                } else if (pilihan == 6) {
                    menuRegresiSpline(sc);
                } else if (pilihan == 7) {
                    menuImageInpainting(sc);
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

    private static void menuImageInpainting(Scanner sc) {
        System.out.println("\n--- Image Inpainting ---");
    
        String fileAsli = readNamaFile(sc, "Masukkan nama file gambar asli: ", true);
        if (fileAsli == null)
            return;

        String fileMask = readNamaFile(sc, "Masukkan nama file gambar mask: ", true);
        if (fileMask == null)
            return;

        String fileOutput = readNamaFile(sc, "Masukkan nama file output: ", false);
        if (fileOutput == null)
            return;

        if (!ImageInpainting.imageInpainting(fileAsli, fileMask, fileOutput)){
            System.out.println("Restorasi gambar tidak berhasil.");
        }
    }

    private static String readNamaFile(Scanner sc, String prompt, boolean foundFile){
        while(true){
            System.out.print(prompt);
            String nama = sc.nextLine().trim();

            if(nama.isEmpty()){
                System.out.println("Nama file tidak boleh kosong!");
                continue;
            }

            int dot = nama.lastIndexOf('.');
            if(dot <= 0 || dot == nama.length() - 1){
                System.out.println("Nama file harus menyertakan format, contoh: gambar.png");
                continue;
            }

            if(foundFile) {
                File f = new File(ImageInpainting.IMAGE_DIR + nama);
                if (!f.isFile()) {
                    System.out.println("File tidak ditemukan: " + f.getPath());
                    continue;
                }
            }
            else{
                String ext = nama.substring(dot + 1).toLowerCase();
                if(!ext.equals("png") && !ext.equals("jpg") && !ext.equals("jpeg")) {
                    System.out.println("Format output harus png, jpg, atau jpeg.");
                    continue;
                }
            }

            return nama;
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

    private static void menuInterpolasi(Scanner sc) {
        System.out.println("\n--- Interpolasi Polinomial ---");
        int n = IOHandler.readInt(sc, "Masukkan jumlah titik (N): ", 1, 100);
        
        System.out.println("Masukkan titik-titik (x y):");
        Matrix m = null;
        try {
            m = IOHandler.readMatrixKeyboard(sc, n, 2);
        } catch (Exception e) {
            System.out.println("Gagal membaca titik: " + e.getMessage());
            return;
        }
        
        double[][] points = new double[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = m.getElmt(i, 0);
            points[i][1] = m.getElmt(i, 1);
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (points[j][0] > points[j+1][0]) {
                    double[] temp = points[j];
                    points[j] = points[j+1];
                    points[j+1] = temp;
                }
            }
        }

        for (int i = 0; i < n - 1; i++) {
            if (Math.abs(points[i+1][0] - points[i][0]) < 1e-9) {
                System.out.println("Terdapat titik x yang duplikat, tidak bisa dilanjutkan.");
                return;
            }
        }
        
        System.out.print("Masukkan nilai x yang ditaksir: ");
        double xTarget = 0;
        try {
            xTarget = IOHandler.parseNumber(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Input tidak valid: " + e.getMessage());
            return;
        }
        
        try {
            Matrix aug = InterpolasiPolinomial.createAugmentedMatrix(points);
            SPLResult res = SPL.gaussJordan(aug);
            
            if (res.jenis != SPLResult.Jenis.UNIQUE) {
                System.out.println("Titik-titik tidak menghasilkan solusi unik (matriks singular).");
                return;
            }
            
            double[] koef = res.konstanta;
            String eq = InterpolasiPolinomial.getEquationString(koef);
            double val = InterpolasiPolinomial.evaluate(koef, xTarget);
            
            System.out.println("\nPersamaan Interpolasi:");
            System.out.println(eq);
            System.out.println("Nilai p(" + IOHandler.formatNumber(xTarget) + ") = " + IOHandler.formatNumber(val));
            
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void menuSpline(Scanner sc) {
        System.out.println("\n--- Natural Cubic Spline Interpolation ---");
        int n = IOHandler.readInt(sc, "Masukkan jumlah titik (N): ", 2, 100);
        
        System.out.println("Masukkan titik-titik (x y):");
        Matrix m = null;
        try {
            m = IOHandler.readMatrixKeyboard(sc, n, 2);
        } catch (Exception e) {
            System.out.println("Gagal membaca titik: " + e.getMessage());
            return;
        }
        
        double[][] points = new double[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = m.getElmt(i, 0);
            points[i][1] = m.getElmt(i, 1);
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (points[j][0] > points[j+1][0]) {
                    double[] temp = points[j];
                    points[j] = points[j+1];
                    points[j+1] = temp;
                }
            }
        }

        for (int i = 0; i < n - 1; i++) {
            if (Math.abs(points[i+1][0] - points[i][0]) < 1e-9) {
                System.out.println("Terdapat titik x yang duplikat, tidak bisa dilanjutkan.");
                return;
            }
        }
        
        System.out.print("Masukkan nilai x yang ditaksir: ");
        double xTarget = 0;
        try {
            xTarget = IOHandler.parseNumber(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Input tidak valid: " + e.getMessage());
            return;
        }
        
        try {
            Matrix aug = SplineKubik.createTridiagonalMatrix(points);
            SPLResult res = SPL.gaussJordan(aug);
            
            if (res.jenis != SPLResult.Jenis.UNIQUE) {
                System.out.println("Gagal membentuk spline (matriks singular).");
                return;
            }
            
            double[] M = res.konstanta;
            
            System.out.println("\nPersamaan Spline tiap segmen:");
            for (int i = 0; i < n - 1; i++) {
                double xi = points[i][0];
                double xNext = points[i+1][0];
                double yi = points[i][1];
                double yNext = points[i+1][1];
                
                double hi = xNext - xi;
                
                double a = yi;
                double b = (yNext - yi) / hi - (2 * M[i] + M[i+1]) * hi / 6.0;
                double c = M[i] / 2.0;
                double d = (M[i+1] - M[i]) / (6.0 * hi);
                
                String eq = String.format("S%d(x) = %s + %s(x - %s) + %s(x - %s)^2 + %s(x - %s)^3",
                    i+1,
                    IOHandler.formatNumber(a),
                    IOHandler.formatNumber(b),
                    IOHandler.formatNumber(xi),
                    IOHandler.formatNumber(c),
                    IOHandler.formatNumber(xi),
                    IOHandler.formatNumber(d),
                    IOHandler.formatNumber(xi)
                );
                
                System.out.println(String.format("Segmen %d [%s, %s]:", i+1, IOHandler.formatNumber(xi), IOHandler.formatNumber(xNext)));
                System.out.println(eq.replace("+ -", "- "));
            }
            
            double val = 0;
            boolean found = false;
            for (int i = 0; i < n - 1; i++) {
                if (xTarget >= points[i][0] && xTarget <= points[i+1][0]) {
                    double xi = points[i][0];
                    double hi = points[i+1][0] - xi;
                    double yi = points[i][1];
                    double yNext = points[i+1][1];
                    double a = yi;
                    double b = (yNext - yi) / hi - (2 * M[i] + M[i+1]) * hi / 6.0;
                    double c = M[i] / 2.0;
                    double d = (M[i+1] - M[i]) / (6.0 * hi);
                    
                    double diff = xTarget - xi;
                    val = a + b * diff + c * Math.pow(diff, 2) + d * Math.pow(diff, 3);
                    found = true;
                    break;
                }
            }
            if (!found) {
                int i = (xTarget < points[0][0]) ? 0 : n - 2;
                double xi = points[i][0];
                double hi = points[i+1][0] - xi;
                double yi = points[i][1];
                double yNext = points[i+1][1];
                double a = yi;
                double b = (yNext - yi) / hi - (2 * M[i] + M[i+1]) * hi / 6.0;
                double c = M[i] / 2.0;
                double d = (M[i+1] - M[i]) / (6.0 * hi);
                
                double diff = xTarget - xi;
                val = a + b * diff + c * Math.pow(diff, 2) + d * Math.pow(diff, 3);
            }
            
            System.out.println("\nNilai S(" + IOHandler.formatNumber(xTarget) + ") = " + IOHandler.formatNumber(val));
            
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void menuRegresiSpline(Scanner sc) {
        System.out.println("\n--- Regresi Spline Kubik ---");
        int n = IOHandler.readInt(sc, "Masukkan jumlah titik (N): ", 1, 100);
        
        System.out.println("Masukkan titik-titik (x y):");
        Matrix m = null;
        try {
            m = IOHandler.readMatrixKeyboard(sc, n, 2);
        } catch (Exception e) {
            System.out.println("Gagal membaca titik: " + e.getMessage());
            return;
        }
        
        double[][] points = new double[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = m.getElmt(i, 0);
            points[i][1] = m.getElmt(i, 1);
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (points[j][0] > points[j+1][0]) {
                    double[] temp = points[j];
                    points[j] = points[j+1];
                    points[j+1] = temp;
                }
            }
        }
        
        int k = IOHandler.readInt(sc, "Masukkan jumlah knot (k): ", 1, 100);
        double[] knots = new double[k];
        for (int i = 0; i < k; i++) {
            System.out.print("Masukkan knot ke-" + (i+1) + ": ");
            try {
                knots[i] = IOHandler.parseNumber(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Input tidak valid: " + e.getMessage());
                return;
            }
        }
        
        System.out.print("Masukkan nilai x yang ditaksir: ");
        double xTarget = 0;
        try {
            xTarget = IOHandler.parseNumber(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Input tidak valid: " + e.getMessage());
            return;
        }
        
        try {
            Matrix X = RegresiSpline.createDesignMatrix(points, knots);
            Matrix Y = RegresiSpline.createYMatrix(points);
            Matrix beta = RegresiSpline.calculateBeta(X, Y);
            
            if (beta == null) {
                System.out.println("Matriks singular, regresi gagal.");
                return;
            }
            
            System.out.println("\nKoefisien Model (Beta):");
            for (int i = 0; i < beta.getRows(); i++) {
                System.out.println("b" + i + " = " + IOHandler.formatNumber(beta.getElmt(i, 0)));
            }
            
            double val = RegresiSpline.evaluate(beta, knots, xTarget);
            System.out.println("\nNilai taksiran y untuk x = " + IOHandler.formatNumber(xTarget) + " adalah " + IOHandler.formatNumber(val));
            
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
}