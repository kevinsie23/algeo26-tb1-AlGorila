package algeo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.math.RoundingMode;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
public class IOHandler {

    public static double parseNumber(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token input tidak boleh kosong.");
        }

        String originalToken = token;
        token = token.trim();
        
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Token hanya berisi spasi kosong: '" + originalToken + "'");
        }
        
        boolean hasDecimal = false;
        boolean hasDigit = false;
        
        int start = 0;
        if (token.charAt(0) == '+' || token.charAt(0) == '-') {
            start = 1;
            if (token.length() == 1) {
                throw new IllegalArgumentException("Token tidak valid, hanya mengandung tanda plus/minus: '" + originalToken + "'");
            }
        }
        
        StringBuilder normalized = new StringBuilder();
        if (start == 1) {
            normalized.append(token.charAt(0));
        }
        
        for (int i = start; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c >= '0' && c <= '9') {
                hasDigit = true;
                normalized.append(c);
            } else if (c == '.' || c == ',') {
                if (hasDecimal) {
                    throw new IllegalArgumentException("Pemisah desimal lebih dari satu pada token: '" + originalToken + "'");
                }
                hasDecimal = true;
                normalized.append('.');
            } else {
                throw new IllegalArgumentException("Karakter tidak valid '" + c + "' ditemukan pada token: '" + originalToken + "'");
            }
        }
        
        if (!hasDigit) {
            throw new IllegalArgumentException("Tidak ada angka yang ditemukan pada token: '" + originalToken + "'");
        }
        
        try {
            double result = Double.parseDouble(normalized.toString());
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                throw new IllegalArgumentException("Nilai angka di luar batas valid (Infinity/NaN): '" + originalToken + "'");
            }
            return result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Gagal mem-parsing token menjadi angka: '" + originalToken + "'", e);
        }
    }

    public static String formatNumber(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        DecimalFormat df = new DecimalFormat("0.###", symbols);
        df.setRoundingMode(RoundingMode.HALF_UP);
        
        String formatted = df.format(value);
        if (formatted.equals("-0")) {
            return "0";
        }
        return formatted;
    }

    public static Matrix readMatrixFile(String path, int maxRows, int maxCols) throws IOException {
        List<double[]> rowList = new ArrayList<>();
        int expectedCols = -1;
        boolean hasData = false;
        
        FileInputStream fis;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File tidak ditemukan atau tidak dapat dibuka: " + path);
        }
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            boolean firstLine = true;
            boolean seenEmptyLine = false;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (firstLine) {
                    if (line.startsWith("\uFEFF")) {
                        line = line.substring(1);
                    }
                    firstLine = false;
                }
                
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    seenEmptyLine = true;
                    continue;
                }
                
                if (seenEmptyLine) {
                    throw new IllegalArgumentException("Ditemukan baris kosong sebelum baris " + lineNumber + " yang berisi data.");
                }
                
                String[] tokens = trimmed.split("\\s+");
                int currentCols = tokens.length;
                
                if (expectedCols == -1) {
                    expectedCols = currentCols;
                } else if (currentCols != expectedCols) {
                    throw new IllegalArgumentException("Jumlah kolom tidak konsisten pada baris " + lineNumber + ". Diharapkan " + expectedCols + ", tetapi ditemukan " + currentCols);
                }
                
                if (rowList.size() >= maxRows) {
                    throw new IllegalArgumentException("Jumlah baris melebihi batas maksimal (" + maxRows + ") pada baris " + lineNumber);
                }
                if (expectedCols > maxCols) {
                    throw new IllegalArgumentException("Jumlah kolom melebihi batas maksimal (" + maxCols + ")");
                }
                
                double[] rowData = new double[currentCols];
                for (int i = 0; i < currentCols; i++) {
                    try {
                        rowData[i] = parseNumber(tokens[i]);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Kesalahan pada baris " + lineNumber + ", kolom " + (i + 1) + ": " + e.getMessage(), e);
                    }
                }
                rowList.add(rowData);
                hasData = true;
            }
            
            if (!hasData) {
                throw new IllegalArgumentException("File kosong atau tidak memiliki data matriks yang valid.");
            }
            
            int numRows = rowList.size();
            Matrix matrix = new Matrix(numRows, expectedCols);
            for (int i = 0; i < numRows; i++) {
                double[] row = rowList.get(i);
                for (int j = 0; j < expectedCols; j++) {
                    matrix.setElmt(i, j, row[j]);
                }
            }
            
            return matrix;
        }
    }

    public static int readInt(Scanner sc, String prompt, int min, int max) {
        while(true){
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                int value = Integer.parseInt(input.trim());
                if(value<min || value>max){
                    System.out.println("Input harus berada di rentang " +min+ " sampai " +max+ ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e){
                System.out.println("Input harus berupa bilangan bulat.");
                continue;
            }

        }
    }
    
    public static Matrix readMatrixKeyboard(Scanner sc, int rows, int cols) {
        Matrix matrix = new Matrix(rows, cols);
        for(int i=0; i<rows; i++){
            while(true){
                if(!sc.hasNextLine()){
                    throw new IllegalStateException("Input berakhir mendadak pada baris ke-" +(i+1));
                }
                String line = sc.nextLine().trim();
                if(line.isEmpty()){
                    System.out.println("Baris " +(i+1)+ " kosong. Diharapkan " +cols+ " angka.");
                    continue;
                }
                String[] tokens = line.split("\\s+");
                if(tokens.length!=cols){
                    System.out.println("Baris " +(i+1)+ " salah. Diharapkan " +cols+ " angka, ditemukan " +tokens.length+ ".");
                    continue;
                }
                boolean hasError = false;
                double[] temp = new double[cols];
                for(int j=0; j<cols; j++){
                    try {
                        temp[j] = parseNumber(tokens[j]);
                    } catch (IllegalArgumentException e){
                        System.out.println("Kesalahan pada baris " +(i+1)+ ": " +e.getMessage());
                        hasError = true;
                        break;
                    }
                }
                if(hasError){
                    continue;
                }
                for(int j=0; j<cols; j++){
                    matrix.setElmt(i, j, temp[j]);
                }
                break;
            }
        }
        return matrix;
    }
}
