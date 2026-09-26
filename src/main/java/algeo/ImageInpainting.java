package algeo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;

public class ImageInpainting {
    public static void restoreImage(String originalPath, String maskPath, String resultPath){
        try{
            File fileImg = new File(originalPath);
            File fileMask = new File(maskPath);

            if(!fileImg.exists()){
                System.out.println("Error: File gambar asli tidak ditemukan!");
                return;
            }
            if(!fileMask.exists()){
                System.out.println("Error: File gambar mask tidak ditemukan!");
                return;
            }

            BufferedImage img = ImageIO.read(fileImg);
            BufferedImage mask = ImageIO.read(fileMask);

            int width = img.getWidth();
            int height = img.getHeight();

            if(width > 512 || height > 512){
                System.out.println("Error: Dimensi gambar melebihi batas!");
                return;
            }

            if(width != mask.getWidth() || height != mask.getHeight()){
                System.out.println("Error: Dimensi gambar asli dan mask tidak sama!");
                return;
            }

            double[][] R = new double[width][height];
            double[][] G = new double[width][height];
            double[][] B = new double[width][height];

            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){ 
                    int color = img.getRGB(i, j);
                    int maskVal = mask.getRGB(i, j) & 0xFF;

                    if(maskVal > 200){
                        R[i][j] = 0.0;
                        G[i][j] = 0.0;
                        B[i][j] = 0.0;
                    }
                    else{
                        R[i][j] = (color >> 16) & 0xFF;
                        G[i][j] = (color >> 8) & 0xFF;
                        B[i][j] = color & 0xFF;
                    }
                }
            }

            int iterasi = 0;
            int maxIterasi = 10000;
            double toleransi = 0.0005;
            double maxError = 999.9;
            
            while(maxError > toleransi && iterasi < maxIterasi){
                maxError = 0.0;

                for(int i = 1; i < width - 1; i++){
                    for(int j = 1; j < height - 1; j++){
                        int maskVal = mask.getRGB(i, j) & 0xFF;

                        if(maskVal > 200){
                            double oldR = R[i][j];
                            double oldG = G[i][j];
                            double oldB = B[i][j];
                            
                            R[i][j] = (R[i-1][j] + R[i+1][j] + R[i][j-1] + R[i][j+1]) / 4.0;
                            G[i][j] = (G[i-1][j] + G[i+1][j] + G[i][j-1] + G[i][j+1]) / 4.0;
                            B[i][j] = (B[i-1][j] + B[i+1][j] + B[i][j-1] + B[i][j+1]) / 4.0;
                        
                            double errorR = Math.abs(oldR - R[i][j]);
                            double errorG = Math.abs(oldG - G[i][j]);
                            double errorB = Math.abs(oldB - B[i][j]);

                            double curError = Math.max(errorR, Math.max(errorG, errorB));
                            if(curError > maxError)
                                    maxError = curError;
                        }
                    }
                }
                iterasi++;
            }

            System.out.println(maxError);
            System.out.println(iterasi);

            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    int maskVal = mask.getRGB(i, j) & 0xFF;
                    if(maskVal == 255){
                        int r = Math.min(255, Math.max(0, (int) Math.round(R[i][j])));
                        int g = Math.min(255, Math.max(0, (int) Math.round(G[i][j])));
                        int b = Math.min(255, Math.max(0, (int) Math.round(B[i][j])));

                        int newColor = (255 << 24) | (r << 16) | (g << 8) | b;
                        img.setRGB(i, j, newColor);
                    }
                }
            }

            File result = new File(resultPath);
            String format = resultPath.substring(resultPath.lastIndexOf('.') + 1);
            ImageIO.write(img, format, result);
        } catch (IOException e){
            System.out.println("Gagal memproses gambar: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error tak terduga: " + e.getMessage());
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args){
        String testOriginal = "src/main/java/algeo/Code_Generated_Image.jpg";
        String testMark = "src/main/java/algeo/Code_Generated_Image_Mask.jpg";
        String testOutput = "src/main/java/algeo/Code_Generated_Image_Result.jpg";

        System.out.println("HASILLL");
        restoreImage(testOriginal, testMark, testOutput);
    }
}
