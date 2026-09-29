package algeo.modules;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageInpainting {
 
    static final int MAX_ITER = 20000;
    static final double EPSILON = 1e-3;
    static final int MASK_THRESHOLD = 127;
    static final int MAX_DIMENSION = 512;
 
    public static void main(String[] args) throws Exception {
 
        String pathOriginal = "C:/Things/Semester 3/IF2123 Aljabar Linear dan Geometri/algeo26-tb1-AlGorila/src/main/java/algeo/Ril.png";
        String pathMask = "C:/Things/Semester 3/IF2123 Aljabar Linear dan Geometri/algeo26-tb1-AlGorila/src/main/java/algeo/Mask.png";
        String pathOutput = "C:/Things/Semester 3/IF2123 Aljabar Linear dan Geometri/algeo26-tb1-AlGorila/src/main/java/algeo/Result.png";
 
        BufferedImage original = ImageIO.read(new File(pathOriginal));
        BufferedImage mask = ImageIO.read(new File(pathMask));
 
        if (original == null || mask == null) {
            System.out.println("Pastikan format gambar valid!");
            return;
        }
 
        int width = original.getWidth();
        int height = original.getHeight();
 
        if (width > MAX_DIMENSION || height > MAX_DIMENSION) {
            System.out.println("PERINGATAN: Ukuran gambar " + width + "x" + height + " melebihi batas maksimum " + MAX_DIMENSION + "x" + MAX_DIMENSION + ". Proses dibatalkan.");
            return;
        }
 
        if (mask.getWidth() != width || mask.getHeight() != height) {
            System.out.println("PERINGATAN: Dimensi mask (" + mask.getWidth() + "x" + mask.getHeight() + ") tidak sama dengan dimensi gambar asli (" + width + "x" + height + "). Proses dibatalkan.");
            return;
        }
 
        boolean[][] isHole = new boolean[height][width];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int gray = grayScale(mask.getRGB(i, j));
                if(gray > MASK_THRESHOLD)
                    isHole[j][i] = true;
                else
                    isHole[j][i] = false;
            }
        }
 
        System.out.println("Dimensi gambar     : " + width + " x " + height);
        
        double[][] R = new double[height][width];
        double[][] G = new double[height][width];
        double[][] B = new double[height][width];
 
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int color = original.getRGB(i, j);
                R[j][i] = (color >> 16) & 0xFF;
                G[j][i] = (color >> 8) & 0xFF;
                B[j][i] = color & 0xFF;
 
                if (isHole[j][i]) {
                    R[j][i] = 128;
                    G[j][i] = 128;
                    B[j][i] = 128;
                }
            }
        }
 
        Result rResult = gaussSeidel(R, isHole, width, height);
        Result gResult = gaussSeidel(G, isHole, width, height);
        Result bResult = gaussSeidel(B, isHole, width, height);
 
        int iterasiMaks = Math.max(rResult.iterations, Math.max(gResult.iterations, bResult.iterations));
        double errorAkhir = Math.max(rResult.finalError, Math.max(gResult.finalError, bResult.finalError));
 
        System.out.println("Jumlah iterasi     : " + iterasiMaks);
        System.out.println("Galat akhir        : " + errorAkhir);
 
        BufferedImage hasil = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int r = clamp(R[j][i]);
                int g = clamp(G[j][i]);
                int b = clamp(B[j][i]);
                int rgb = (r << 16) | (g << 8) | b;
                hasil.setRGB(i, j, rgb);
            }
        }

        String format = "";
        if(pathOutput.toLowerCase().endsWith(".jpg") || pathOutput.toLowerCase().endsWith(".jpeg"))
            format = "jpg";
        else
            format = "png";

        ImageIO.write(hasil, format, new File(pathOutput));
        System.out.println("Gambar hasil disimpan di: " + pathOutput);
    }


    static Result gaussSeidel(double[][] channel, boolean[][] isHole, int width, int height) {
        int iter;
        double maxDelta = Double.MAX_VALUE;
 
        for (iter = 1; iter <= MAX_ITER; iter++) {
            maxDelta = 0.0;
 
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    if (!isHole[j][i])
                        continue;
 
                    double sum = 0.0;
                    int count = 0;
 
                    if (j - 1 >= 0){
                        sum += channel[j - 1][i];
                        count++;
                    }
                    if (j + 1 < height){
                        sum += channel[j + 1][i];
                        count++;
                    }
                    if (i - 1 >= 0){
                        sum += channel[j][i - 1];
                        count++;
                    }
                    if (i + 1 < width){
                        sum += channel[j][i + 1];
                        count++;
                    }
 
                    double newValue = sum / count;
                    double delta = Math.abs(newValue - channel[j][i]);
                    if (delta > maxDelta)
                        maxDelta = delta;
 
                    channel[j][i] = newValue;
                }
            }
 
            if (maxDelta < EPSILON)
                break;
        }
 
        return new Result(Math.min(iter, MAX_ITER), maxDelta);
    }
 
    static int grayScale(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (r + g + b) / 3;
    }
 
    static int clamp(double v) {
        if (v < 0) return 0;
        if (v > 255) return 255;
        return (int) Math.round(v);
    }
 
    static class Result {
        int iterations;
        double finalError;

        Result(int iterations, double finalError) {
            this.iterations = iterations;
            this.finalError = finalError;
        }
    }
}
