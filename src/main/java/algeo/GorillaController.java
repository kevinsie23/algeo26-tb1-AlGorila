package algeo;

import algeo.modules.Matrix;
import algeo.modules.SPL;
import algeo.modules.SPLResult;
import algeo.modules.Determinan;
import algeo.modules.Invers;
import algeo.modules.IOHandler;
import algeo.modules.ImageInpainting;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class GorillaController {
    // SPL
    @FXML private RadioButton splRadioManual;
    @FXML private RadioButton splRadioFile;
    @FXML private TextArea splInputArea;
    @FXML private TextField splOutputNamaField;
    
    @FXML private ComboBox<String> splMetodeCombo;
    @FXML private TextArea splOutputArea;
    @FXML private Button splSimpanBtn;

    // Determinan
    @FXML private RadioButton detRadioManual, detRadioFile;
    @FXML private TextArea detInputArea, detOutputArea;
    @FXML private ComboBox<String> detMetodeCombo;
    @FXML private TextField detOutputNamaField;

    // Invers
    @FXML private RadioButton invRadioManual, invRadioFile;
    @FXML private TextArea invInputArea, invOutputArea;
    @FXML private ComboBox<String> invMetodeCombo;
    @FXML private TextField invOutputNamaField;

    // Image Impainting
    @FXML private Label inpaintGambarPathLabel;
    @FXML private ImageView inpaintPreviewGambar;
    @FXML private Label inpaintMaskPathLabel;
    @FXML private ImageView inpaintPreviewMask;
    @FXML private TextField inpaintOutputNamaField;
    @FXML private TextArea inpaintInfoArea;

    //interp
    @FXML private RadioButton interpRadioManual, interpRadioFile;
    @FXML private TextArea interpInputArea, interpOutputArea;
    @FXML private TextField interpXField, interpOutputNamaField;
    @FXML private TextField interpTargetField;
    private String interpSaveContent = "";

    //spline
    @FXML private RadioButton splineRadioManual, splineRadioFile;
    @FXML private TextArea splineInputArea, splineOutputArea;
    @FXML private TextField splineXField, splineOutputNamaField;
    @FXML private TextField splineTargetField;
    private String splineSaveContent = "";

    // regresi
    @FXML private RadioButton regresiRadioManual, regresiRadioFile;
    @FXML private TextArea regresiInputArea, regresiOutputArea;
    @FXML private TextField regresiKnotsField, regresiXField, regresiOutputNamaField;
    @FXML private TextField regresiTargetField;
    private String regresiSaveContent = "";

    private String splSaveContent = "";
    private String detSaveContent = "";
    private String invSaveContent = "";

    @FXML
    private void handleSumberInput(ActionEvent event) {
        if (splRadioFile.isSelected()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih File Input Matriks");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );

            File selectedFile = fileChooser.showOpenDialog(splRadioFile.getScene().getWindow());

            if (selectedFile != null) {
                try {
                    String content = Files.readString(selectedFile.toPath());
                    splInputArea.setText(content);
                    splInputArea.setEditable(false); 
                } catch (Exception e) {
                    splInputArea.setText("Gagal membaca file!");
                }
            } else {
                splRadioManual.setSelected(true);
                splInputArea.setEditable(true);
            }
        } else if (splRadioManual.isSelected()) {
            splInputArea.clear();
            splInputArea.setEditable(true);
        }
    }
    @FXML
    public void initialize() {
        splMetodeCombo.getItems().addAll("Eliminasi Gauss", "Eliminasi Gauss-Jordan", "Metode Matriks Balikan", "Kaidah Cramer");
        splMetodeCombo.getSelectionModel().selectFirst();
        detMetodeCombo.getItems().addAll("Reduksi Baris", "Ekspansi Kofaktor");
        detMetodeCombo.getSelectionModel().selectFirst();
        invMetodeCombo.getItems().addAll("Augmentasi", "Adjoin");
        invMetodeCombo.getSelectionModel().selectFirst();
    }

    private Matrix parseMatrixString(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Input matriks kosong!");
        }
        String[] lines = text.trim().split("\n");
        int rows = lines.length;
        int cols = lines[0].trim().split("\\s+").length;
        
        Matrix matrix = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            String[] tokens = lines[i].trim().split("\\s+");
            if (tokens.length != cols) {
                throw new IllegalArgumentException("Jumlah kolom tidak konsisten di baris " + (i + 1));
            }
            for (int j = 0; j < cols; j++) {
                matrix.setElmt(i, j, IOHandler.parseNumber(tokens[j]));
            }
        }
        return matrix;
    }


    @FXML
    private void handleHitungSPL(ActionEvent event) {
        try {
            Matrix matrix = parseMatrixString(splInputArea.getText());
            String metode = splMetodeCombo.getValue();
            SPLResult result = null;
            
            if ("Eliminasi Gauss".equals(metode)) {
                result = SPL.gauss(matrix);
            } else if ("Eliminasi Gauss-Jordan".equals(metode)) {
                result = SPL.gaussJordan(matrix);
            } else if ("Metode Matriks Balikan".equals(metode)) {
                result = SPL.inverseMethod(matrix);
            } else if ("Kaidah Cramer".equals(metode)) {
                result = SPL.cramer(matrix);
            }
            
            if (result != null) {
                StringBuilder ta = new StringBuilder(); 
                StringBuilder txt = new StringBuilder(); 
                
                String namaMetode = result.namaMetode != null ? result.namaMetode : metode;
                String header = "Metode: " + namaMetode + "\n\nMatriks Input:\n";
                ta.append(header);
                txt.append(header);
                
               
                for (int i = 0; i < matrix.getRows(); i++) {
                    for (int j = 0; j < matrix.getCols(); j++) {
                        String val = IOHandler.formatNumber(matrix.getElmt(i, j));
                        ta.append(val);
                        txt.append(val);
                        if (j < matrix.getCols() - 1) {
                            ta.append(" ");
                            txt.append(" ");
                        }
                    }
                    ta.append("\n");
                    txt.append("\n");
                }
                
                if (result.langkah != null && !result.langkah.isEmpty()) {
                    ta.append("\nLangkah-langkah:\n");
                    for (String step : result.langkah) {
                        ta.append(step).append("\n");
                    }
                }
                
                String hasilAkhir = "\nHasil:\n" + result.toDisplayString(matrix.getCols() - 1);
                ta.append(hasilAkhir);
                txt.append(hasilAkhir);
                
                splOutputArea.setText(ta.toString());
                splSaveContent = txt.toString();     
            }
        } catch (Exception e) {
            splOutputArea.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleHitungDeterminan(ActionEvent event) {
        try {
            Matrix matrix = parseMatrixString(detInputArea.getText());
            if (matrix.getRows() != matrix.getCols()) {
                throw new IllegalArgumentException("matriks tidak memiliki determinan");
            }
            
            String metode = detMetodeCombo.getValue();
            double det = 0;
            String namaMetode = "";
            
            if ("Reduksi Baris".equals(metode)) {
                det = Determinan.rowReduction(matrix);
                namaMetode = "Determinan (Reduksi Baris)";
            } else if ("Ekspansi Kofaktor".equals(metode)) {
                det = Determinan.cofactorExpansion(matrix);
                namaMetode = "Determinan (Ekspansi Kofaktor)";
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Metode: ").append(namaMetode).append("\n\nMatriks Input:\n");
            for (int i = 0; i < matrix.getRows(); i++) {
                for (int j = 0; j < matrix.getCols(); j++) {
                    sb.append(IOHandler.formatNumber(matrix.getElmt(i, j)));
                    if (j < matrix.getCols() - 1) sb.append(" ");
                }
                sb.append("\n");
            }
            sb.append("\nHasil:\nDeterminan = ").append(IOHandler.formatNumber(det));
            
            detOutputArea.setText(sb.toString());
            detSaveContent = sb.toString();
        } catch (Exception e) {
            detOutputArea.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleHitungInvers(ActionEvent event) {
        try {
            Matrix matrix = parseMatrixString(invInputArea.getText());
            if (matrix.getRows() != matrix.getCols()) {
                invOutputArea.setText("matriks tidak memiliki balikan");
                return;
            }
            
            String metode = invMetodeCombo.getValue();
            Matrix invers = null;
            String namaMetode = "";
            
            if ("Augmentasi".equals(metode)) {
                invers = Invers.augmentInverse(matrix);
                namaMetode = "Invers (Augmentasi)";
            } else if ("Adjoin".equals(metode)) {
                invers = Invers.adjoinInverse(matrix);
                namaMetode = "Invers (Adjoin)";
            }
            
            if (invers == null) {
                invOutputArea.setText("matriks tidak memiliki balikan");
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Metode: ").append(namaMetode).append("\n\nMatriks Input:\n");
            for (int i = 0; i < matrix.getRows(); i++) {
                for (int j = 0; j < matrix.getCols(); j++) {
                    sb.append(IOHandler.formatNumber(matrix.getElmt(i, j)));
                    if (j < matrix.getCols() - 1) sb.append(" ");
                }
                sb.append("\n");
            }
            sb.append("\nHasil:\n");  
            for (int i = 0; i < invers.getRows(); i++) {
                for (int j = 0; j < invers.getCols(); j++) {
                    sb.append(IOHandler.formatNumber(invers.getElmt(i, j)));
                    if (j < invers.getCols() - 1) sb.append(" ");
                }
                sb.append("\n");
            }
            
            invOutputArea.setText(sb.toString().trim());
            invSaveContent = sb.toString().trim();
            
        } catch (Exception e) {
            invOutputArea.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSimpanSPL(ActionEvent event) {
        simpanKeFileLangsung(splSaveContent, splOutputNamaField.getText());
    }

    @FXML
    private void handleSimpanDet(ActionEvent event) {
        simpanKeFileLangsung(detSaveContent, detOutputNamaField.getText());
    }

    @FXML
    private void handleSimpanInv(ActionEvent event) {
        simpanKeFileLangsung(invSaveContent, invOutputNamaField.getText());
    }


    private File fileGambarAsli = null;
    private File fileMask = null;

    @FXML
    private void handlePilihGambarAsli(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Pilih Gambar Asli");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        
        File selectedFile = fileChooser.showOpenDialog(inpaintGambarPathLabel.getScene().getWindow());
        if (selectedFile != null) {
            fileGambarAsli = selectedFile;
            inpaintGambarPathLabel.setText(selectedFile.getName());
            
            inpaintPreviewGambar.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void handlePilihMask(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Pilih Mask Gambar");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        
        File selectedFile = fileChooser.showOpenDialog(inpaintMaskPathLabel.getScene().getWindow());
        if (selectedFile != null) {
            fileMask = selectedFile;
            inpaintMaskPathLabel.setText(selectedFile.getName());
            
            inpaintPreviewMask.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void handleProsesInpainting(ActionEvent event) {

        if (fileGambarAsli == null || fileMask == null) {
            inpaintInfoArea.setText("Error: Harap pilih gambar asli dan mask terlebih dahulu");
            return;
        }
        
        String outputName = inpaintOutputNamaField.getText().trim();
        if (outputName.isEmpty()) {
            outputName = "hasil_inpaint.png";
        } else if (!outputName.toLowerCase().endsWith(".png") && 
                   !outputName.toLowerCase().endsWith(".jpg") && 
                   !outputName.toLowerCase().endsWith(".jpeg")) {
            outputName += ".png";
        }

        try {
            inpaintInfoArea.setText("Sedang memproses inpainting dengan Gauss-Seidel...\nCek terminal untuk melihat progres iterasi.");
            
            String outputPath = fileGambarAsli.getParent() + File.separator + outputName; 
            
            boolean sukses = ImageInpainting.imageInpainting(
                fileGambarAsli.getAbsolutePath(), 
                fileMask.getAbsolutePath(), 
                outputPath
            );
            
            if (sukses) {
                inpaintInfoArea.setText("Inpainting Selesai!\nTersimpan di:\n" + outputPath);
                showImagePopup(outputPath);
            } else {
                inpaintInfoArea.setText("Proses dibatalkan atau gagal.\nCek peringatan di terminal (misal: dimensi beda atau ukuran > 512x512).");
            }

        } catch (Exception e) {
            inpaintInfoArea.setText("Error saat proses inpainting: " + e.getMessage());
        }
    }

    private void showImagePopup(String imagePath) {
        try {
            File imgFile = new File(imagePath);
            if (!imgFile.exists()) {
                return; 
            }

            Stage popupStage = new Stage();
            popupStage.setTitle("Hasil Image Inpainting");

            ImageView imageView = new ImageView(new Image(imgFile.toURI().toString()));
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(800); 
            imageView.setFitHeight(600); 

            StackPane layout = new StackPane();
            layout.getChildren().add(imageView);

            Scene scene = new Scene(layout);
            popupStage.setScene(scene);
            popupStage.show();
        } catch (Exception e) {
            inpaintInfoArea.setText("Gagal menampilkan jendela popup: " + e.getMessage());
        }
    }

    private void simpanKeFileLangsung(String content, String namaFile) {
        if (content == null || content.trim().isEmpty()) {
            System.out.println("Output kosong, tidak ada yang disimpan.");
            return; 
        }
        
        if (namaFile == null || namaFile.trim().isEmpty()) {
            namaFile = "hasil_output.txt"; 
        } else if (!namaFile.toLowerCase().endsWith(".txt")) {
            namaFile += ".txt";
        }

        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(namaFile))) {
            writer.print(content);
            System.out.println("Berhasil menyimpan file ke: " + new java.io.File(namaFile).getAbsolutePath());
        } catch (java.io.IOException e) {
            System.out.println("Gagal menyimpan file: " + e.getMessage());
        }
    }

    @FXML
    private void handleHitungInterpolasi(ActionEvent event) {
        try {
            Matrix pointsMat = parseMatrixString(interpInputArea.getText());
            int n = pointsMat.getRows();
            double[][] points = new double[n][2];
            for (int i = 0; i < n; i++) {
                points[i][0] = pointsMat.getElmt(i, 0);
                points[i][1] = pointsMat.getElmt(i, 1);
            }

            Matrix aug = algeo.modules.InterpolasiPolinomial.createAugmentedMatrix(points);
            SPLResult res = SPL.gaussJordan(aug);

            if (res.jenis != SPLResult.Jenis.UNIQUE) {
                interpOutputArea.setText("Titik-titik tidak menghasilkan solusi unik (matriks singular).");
                return;
            }

            double[] koef = res.konstanta;
            String eq = algeo.modules.InterpolasiPolinomial.getEquationString(koef);
            
            StringBuilder sb = new StringBuilder();
            sb.append("Persamaan Interpolasi:\n").append(eq).append("\n");

            if (interpTargetField != null && !interpTargetField.getText().trim().isEmpty()) {
                double xTarget = IOHandler.parseNumber(interpTargetField.getText());
                double val = algeo.modules.InterpolasiPolinomial.evaluate(koef, xTarget);
                sb.append("\nNilai P(").append(IOHandler.formatNumber(xTarget)).append(") = ").append(IOHandler.formatNumber(val));
            }

            interpOutputArea.setText(sb.toString());
            interpSaveContent = sb.toString();
        } catch (Exception e) {
            interpOutputArea.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleHitungSpline(ActionEvent event) {
        try {
            Matrix pointsMat = parseMatrixString(splineInputArea.getText());
            int n = pointsMat.getRows();
            double[][] points = new double[n][2];
            for (int i = 0; i < n; i++) {
                points[i][0] = pointsMat.getElmt(i, 0);
                points[i][1] = pointsMat.getElmt(i, 1);
            }

            Matrix aug = algeo.modules.SplineKubik.createTridiagonalMatrix(points);
            SPLResult res = SPL.gaussJordan(aug);

            if (res.jenis != SPLResult.Jenis.UNIQUE) {
                splineOutputArea.setText("Gagal membentuk spline (matriks singular).");
                return;
            }

            double[] M = res.konstanta;
            StringBuilder sb = new StringBuilder();
            sb.append("Persamaan Spline tiap segmen:\n");
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
                    i+1, IOHandler.formatNumber(a), IOHandler.formatNumber(b), IOHandler.formatNumber(xi),
                    IOHandler.formatNumber(c), IOHandler.formatNumber(xi), IOHandler.formatNumber(d), IOHandler.formatNumber(xi));
                
                sb.append(String.format("Segmen %d [%s, %s]:\n", i+1, IOHandler.formatNumber(xi), IOHandler.formatNumber(xNext)));
                sb.append(eq.replace("+ -", "- ")).append("\n\n");
            }

            if (splineTargetField != null && !splineTargetField.getText().trim().isEmpty()) {
                double xTarget = IOHandler.parseNumber(splineTargetField.getText());
                double val = 0;
                boolean found = false;
                for (int i = 0; i < n - 1; i++) {
                    if (xTarget >= points[i][0] && xTarget <= points[i+1][0]) {
                        double xi = points[i][0], hi = points[i+1][0] - xi;
                        double yi = points[i][1], yNext = points[i+1][1];
                        double a = yi, b = (yNext - yi) / hi - (2 * M[i] + M[i+1]) * hi / 6.0;
                        double c = M[i] / 2.0, d = (M[i+1] - M[i]) / (6.0 * hi);
                        double diff = xTarget - xi;
                        val = a + b * diff + c * Math.pow(diff, 2) + d * Math.pow(diff, 3);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    int i = (xTarget < points[0][0]) ? 0 : n - 2;
                    double xi = points[i][0], hi = points[i+1][0] - xi;
                    double yi = points[i][1], yNext = points[i+1][1];
                    double a = yi, b = (yNext - yi) / hi - (2 * M[i] + M[i+1]) * hi / 6.0;
                    double c = M[i] / 2.0, d = (M[i+1] - M[i]) / (6.0 * hi);
                    double diff = xTarget - xi;
                    val = a + b * diff + c * Math.pow(diff, 2) + d * Math.pow(diff, 3);
                }
                sb.append("Nilai S(").append(IOHandler.formatNumber(xTarget)).append(") = ").append(IOHandler.formatNumber(val));
            }

            splineOutputArea.setText(sb.toString().trim());
            splineSaveContent = sb.toString().trim();
        } catch (Exception e) {
            splineOutputArea.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleHitungRegresi(ActionEvent event) {
        try {
            Matrix pointsMat = parseMatrixString(regresiInputArea.getText());
            int n = pointsMat.getRows();
            double[][] points = new double[n][2];
            for (int i = 0; i < n; i++) {
                points[i][0] = pointsMat.getElmt(i, 0);
                points[i][1] = pointsMat.getElmt(i, 1);
            }

            String[] knotStrs = regresiKnotsField.getText().trim().split("\\s+");
            double[] knots = new double[knotStrs.length];
            for (int i = 0; i < knotStrs.length; i++) {
                knots[i] = IOHandler.parseNumber(knotStrs[i]);
            }

            Matrix X = algeo.modules.RegresiSpline.createDesignMatrix(points, knots);
            Matrix Y = algeo.modules.RegresiSpline.createYMatrix(points);
            Matrix beta = algeo.modules.RegresiSpline.calculateBeta(X, Y);

            if (beta == null) {
                regresiOutputArea.setText("Matriks singular, regresi gagal.");
                return;
            }

            StringBuilder sb = new StringBuilder("Koefisien Model (Beta):\n");
            for (int i = 0; i < beta.getRows(); i++) {
                sb.append("b").append(i).append(" = ").append(IOHandler.formatNumber(beta.getElmt(i, 0))).append("\n");
            }

            if (regresiTargetField != null && !regresiTargetField.getText().trim().isEmpty()) {
                double xTarget = IOHandler.parseNumber(regresiTargetField.getText());
                double val = algeo.modules.RegresiSpline.evaluate(beta, knots, xTarget);
                sb.append("\nNilai taksiran y untuk x = ").append(IOHandler.formatNumber(xTarget)).append(" adalah ").append(IOHandler.formatNumber(val));
            }

            regresiOutputArea.setText(sb.toString());
            regresiSaveContent = sb.toString(); 
        } catch (Exception e) {
            regresiOutputArea.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSimpanInterp(ActionEvent event) {
        simpanKeFileLangsung(interpSaveContent, interpOutputNamaField.getText());
    }

    @FXML
    private void handleSimpanSpline(ActionEvent event) {
        simpanKeFileLangsung(splineSaveContent, splineOutputNamaField.getText());
    }

    @FXML
    private void handleSimpanRegresi(ActionEvent event) {
        simpanKeFileLangsung(regresiSaveContent, regresiOutputNamaField.getText());
    }
}