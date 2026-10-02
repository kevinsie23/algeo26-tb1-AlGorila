package algeo.modules;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageInpainting {
 
    static final int MAX_ITER = 20000;
    static final double DIF = 1e-3;
    static final int MAX_DIMENSION = 512;
    static final String IMAGE_DIR = "image/";
 
    public static boolean imageInpainting(String pathOriginal, String pathMask, String pathOutput) {
        try{
            if (!new File(pathOriginal).isAbsolute()) {
                pathOriginal = IMAGE_DIR + pathOriginal;
            }
            if (!new File(pathMask).isAbsolute()) {
                pathMask = IMAGE_DIR + pathMask;
            }
            if (!new File(pathOutput).isAbsolute()) {
                pathOutput = IMAGE_DIR + pathOutput;
            }
    
            BufferedImage original = ImageIO.read(new File(pathOriginal));
            BufferedImage mask = ImageIO.read(new File(pathMask));

            if(!isValidImage(original, mask))
                return false;

            int width = original.getWidth();
            int height = original.getHeight();
            System.out.println("Dimensi gambar     : " + width + " x " + height);


            boolean[][] isHole = getMaskHole(mask, width, height);
            
            Matrix R = new Matrix(height, width);
            Matrix G = new Matrix(height, width);
            Matrix B = new Matrix(height, width);
            getRGBOriginal(original, isHole, R, G, B, width, height);

            Result rResult = gaussSeidel(R, isHole, width, height);
            Result gResult = gaussSeidel(G, isHole, width, height);
            Result bResult = gaussSeidel(B, isHole, width, height);
    
            int iterasiMaks = Math.max(rResult.iterations, Math.max(gResult.iterations, bResult.iterations));
            double errorAkhir = Math.max(rResult.finalError, Math.max(gResult.finalError, bResult.finalError));
    
            System.out.println("Jumlah iterasi     : " + iterasiMaks);
            System.out.println("Galat akhir        : " + errorAkhir);

            BufferedImage hasil = combineRGBOriginal(R, G, B, width, height);
            
            String format = "";
            if(pathOutput.toLowerCase().endsWith(".jpg") || pathOutput.toLowerCase().endsWith(".jpeg"))
                format = "jpg";
            else
                format = "png";

            ImageIO.write(hasil, format, new File(pathOutput));
            System.out.println("Gambar hasil disimpan di: " + pathOutput);
            return true;

        } catch (Exception e){
            System.out.println("Terjadi kesalahan saat memproses gambar: " + e.getMessage());
            return false;
        }
    }



    private static boolean isValidImage(BufferedImage original, BufferedImage mask){
        if (original == null || mask == null) {
            System.out.println("Pastikan format gambar valid!");
            return false;
        }
 
        int width = original.getWidth();
        int height = original.getHeight();
 
        if (width > MAX_DIMENSION || height > MAX_DIMENSION) {
            System.out.println("PERINGATAN: Ukuran gambar " + width + "x" + height + " melebihi batas maksimum " + MAX_DIMENSION + "x" + MAX_DIMENSION + ". Proses dibatalkan.");
            return false;
        }
 
        if (mask.getWidth() != width || mask.getHeight() != height) {
            System.out.println("PERINGATAN: Dimensi mask (" + mask.getWidth() + "x" + mask.getHeight() + ") tidak sama dengan dimensi gambar asli (" + width + "x" + height + "). Proses dibatalkan.");
            return false;
        }

        return true;
    }



    private static boolean[][] getMaskHole(BufferedImage mask, int width, int height){
        boolean[][] isHole = new boolean[height][width];
        
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int gray = grayScale(mask.getRGB(i, j));
                if(gray != 0)
                    isHole[j][i] = true;
                else
                    isHole[j][i] = false;
            }
        }

        return isHole;
    }



    private static void getRGBOriginal(BufferedImage original, boolean[][] isHole, Matrix R, Matrix G, Matrix B, int width, int height){
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int color = original.getRGB(i, j);
                R.setElmt(j, i, (color >> 16) & 0xFF);
                G.setElmt(j, i, (color >> 8) & 0xFF);
                B.setElmt(j, i, color & 0xFF);
                
                if (isHole[j][i]) {
                    R.setElmt(j, i, 128);
                    G.setElmt(j, i, 128);
                    B.setElmt(j, i, 128);
                }
            }
        }

        return;
    }



    private static Result gaussSeidel(Matrix channel, boolean[][] isHole, int width, int height) {
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
                        sum += channel.getElmt(j - 1, i);
                        count++;
                    }
                    if (j + 1 < height){
                        sum += channel.getElmt(j + 1, i);
                        count++;
                    }
                    if (i - 1 >= 0){
                        sum += channel.getElmt(j, i - 1);
                        count++;
                    }
                    if (i + 1 < width){
                        sum += channel.getElmt(j, i+ 1);
                        count++;
                    }
 
                    double newValue = sum / count;
                    double oldValue = channel.getElmt(j, i);
                    double delta = Math.abs(newValue - oldValue);
                    if (delta > maxDelta)
                        maxDelta = delta;
 
                    channel.setElmt(j, i, newValue);;
                }
            }
 
            if (maxDelta < DIF)
                break;
        }
 
        return new Result(Math.min(iter, MAX_ITER), maxDelta);
    }



    private static BufferedImage combineRGBOriginal(Matrix R, Matrix G, Matrix B, int width, int height){
        BufferedImage hasil = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int r = clamp(R.getElmt(j, i));
                int g = clamp(G.getElmt(j, i));
                int b = clamp(B.getElmt(j, i));
                int rgb = (r << 16) | (g << 8) | b;
                hasil.setRGB(i, j, rgb);
            }
        }

        return hasil;
    }


 
    private static int grayScale(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (r + g + b) / 3;
    }
 


    private static int clamp(double v) {
        if (v < 0) return 0;
        if (v > 255) return 255;
        return (int) Math.round(v);
    }
 
    private static class Result {
        int iterations;
        double finalError;

        Result(int iterations, double finalError) {
            this.iterations = iterations;
            this.finalError = finalError;
        }
    }
}
