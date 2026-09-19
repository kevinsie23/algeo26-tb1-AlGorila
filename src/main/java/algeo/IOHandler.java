package algeo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.math.RoundingMode;

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
}
