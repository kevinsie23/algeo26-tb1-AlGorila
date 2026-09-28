package algeo;
import algeo.SPLResult.Jenis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPL {
    public static Matrix rref(Matrix m, int pivotCols, List<String> stepsOrNull) {
        Matrix temp = m.copy();
        int rows = temp.getRows();
        int cols = temp.getCols();
        
        int r = 0;
        for(int c=0; c<pivotCols && r<rows; c++){
            int pivotRow = r;
            double maxVal = Math.abs(temp.getElmt(r, c));
            for(int i=r+1; i<rows; i++){
                double val = Math.abs(temp.getElmt(i, c));
                if(val > maxVal){
                    maxVal = val;
                    pivotRow = i;
                }
            }
            
            if(Math.abs(temp.getElmt(pivotRow, c)) < 1e-9){
                continue;
            }
            
            if(pivotRow != r){
                temp.swapRows(r, pivotRow);
                if(stepsOrNull != null){
                    stepsOrNull.add("R" + (r+1) + " <-> R" + (pivotRow+1));
                    stepsOrNull.add(matriksKeString(temp));
                }
            }
            
            double pivotVal = temp.getElmt(r, c);
            if(Math.abs(pivotVal - 1.0) > 1e-9){
                temp.multiplyRow(r, 1.0 / pivotVal);
                if(stepsOrNull != null){
                    stepsOrNull.add("R" + (r+1) + " <- R" + (r+1) + " / " + IOHandler.formatNumber(pivotVal));
                    stepsOrNull.add(matriksKeString(temp));
                }
            }
            
            for(int i=0; i<rows; i++){
                if(i == r) continue;
                double factor = temp.getElmt(i, c);
                if(Math.abs(factor) > 1e-9){
                    temp.addMulRow(i, r, -factor);
                    if(stepsOrNull != null){
                        String sign = factor > 0 ? " - " : " + ";
                        stepsOrNull.add("R" + (i+1) + " <- R" + (i+1) + sign + IOHandler.formatNumber(Math.abs(factor)) + "*R" + (r+1));
                        stepsOrNull.add(matriksKeString(temp));
                    }
                }
            }
            r++;
        }
        
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                if(Math.abs(temp.getElmt(i, j)) < 1e-9){
                    temp.setElmt(i, j, 0.0);
                }
            }
        }
        
        return temp;
    }

    public static SPLResult gauss(Matrix aug) {
        SPLResult res = new SPLResult();
        res.namaMetode = "Eliminasi Gauss";
        
        Matrix temp = aug.copy();
        int rows = temp.getRows();
        int cols = temp.getCols();
        int varCount = cols - 1;
        
        boolean recordSteps = rows <= 12;
        if(!recordSteps){
            res.langkah.add("Matriks besar, langkah detail tidak ditampilkan.");
        }
        
        int r = 0;
        int[] pivotCols = new int[rows];
        for(int i=0; i<rows; i++){
            pivotCols[i] = -1;
        }
        
        for(int c=0; c<varCount && r<rows; c++){
            int pivotRow = r;
            double maxVal = Math.abs(temp.getElmt(r, c));
            for(int i=r+1; i<rows; i++){
                double val = Math.abs(temp.getElmt(i, c));
                if(val > maxVal){
                    maxVal = val;
                    pivotRow = i;
                }
            }
            
            if(Math.abs(temp.getElmt(pivotRow, c)) < 1e-9){
                continue;
            }
            
            if(pivotRow != r){
                temp.swapRows(r, pivotRow);
                if(recordSteps){
                    res.langkah.add("R" + (r+1) + " <-> R" + (pivotRow+1));
                    res.langkah.add(matriksKeString(temp));
                }
            }
            
            double pivotVal = temp.getElmt(r, c);
            if(Math.abs(pivotVal - 1.0) > 1e-9){
                temp.multiplyRow(r, 1.0 / pivotVal);
                if(recordSteps){
                    res.langkah.add("R" + (r+1) + " <- R" + (r+1) + " / " + IOHandler.formatNumber(pivotVal));
                    res.langkah.add(matriksKeString(temp));
                }
            }
            pivotCols[r] = c;
            
            for(int i=r+1; i<rows; i++){
                double factor = temp.getElmt(i, c);
                if(Math.abs(factor) > 1e-9){
                    temp.addMulRow(i, r, -factor);
                    if(recordSteps){
                        String sign = factor > 0 ? " - " : " + ";
                        res.langkah.add("R" + (i+1) + " <- R" + (i+1) + sign + IOHandler.formatNumber(Math.abs(factor)) + "*R" + (r+1));
                        res.langkah.add(matriksKeString(temp));
                    }
                }
            }
            r++;
        }
        
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                if(Math.abs(temp.getElmt(i, j)) < 1e-9){
                    temp.setElmt(i, j, 0.0);
                }
            }
        }
        
        for(int i=0; i<rows; i++){
            boolean allZero = true;
            for(int j=0; j<varCount; j++){
                if(Math.abs(temp.getElmt(i, j)) > 1e-9){
                    allZero = false;
                    break;
                }
            }
            if(allZero && Math.abs(temp.getElmt(i, cols-1)) > 1e-9){
                res.jenis = Jenis.NONE;
                return res;
            }
        }
        
        boolean[] isPivot = new boolean[varCount];
        for(int i=0; i<rows; i++){
            if(pivotCols[i] != -1){
                isPivot[pivotCols[i]] = true;
            }
        }
        
        boolean hasFree = false;
        res.namaParameter = new ArrayList<>();
        int paramIndex = 0;
        int[] varToParamIndex = new int[varCount];
        for(int c=0; c<varCount; c++){
            if(!isPivot[c]){
                hasFree = true;
                res.namaParameter.add(SPLResult.namaParameterKe(paramIndex));
                varToParamIndex[c] = paramIndex;
                paramIndex++;
            } else {
                varToParamIndex[c] = -1;
            }
        }
        
        res.jenis = hasFree ? Jenis.INFINITE : Jenis.UNIQUE;
        res.konstanta = new double[varCount];
        res.koefisienParam = new ArrayList<>();
        for(int i=0; i<varCount; i++){
            res.koefisienParam.add(new HashMap<>());
        }
        
        for(int c=varCount-1; c>=0; c--){
            if(!isPivot[c]){
                res.konstanta[c] = 0.0;
                res.koefisienParam.get(c).put(res.namaParameter.get(varToParamIndex[c]), 1.0);
                continue;
            }
            
            int rowIdx = -1;
            for(int i=0; i<rows; i++){
                if(pivotCols[i] == c){
                    rowIdx = i;
                    break;
                }
            }
            
            double val = temp.getElmt(rowIdx, cols-1);
            Map<String, Double> map = res.koefisienParam.get(c);
            
            for(int j=c+1; j<varCount; j++){
                double coeff = temp.getElmt(rowIdx, j);
                if(Math.abs(coeff) < 1e-9) continue;
                
                val -= coeff * res.konstanta[j];
                
                Map<String, Double> mapJ = res.koefisienParam.get(j);
                for(Map.Entry<String, Double> entry : mapJ.entrySet()){
                    String pName = entry.getKey();
                    double pVal = entry.getValue();
                    double current = map.getOrDefault(pName, 0.0);
                    map.put(pName, current - coeff * pVal);
                }
            }
            
            if(Math.abs(val) < 1e-9) val = 0.0;
            res.konstanta[c] = val;
            
            for(Map.Entry<String, Double> entry : map.entrySet()){
                if(Math.abs(entry.getValue()) < 1e-9){
                    map.put(entry.getKey(), 0.0);
                }
            }
        }
        
        return res;
    }

    public static SPLResult gaussJordan(Matrix aug) {
        SPLResult res = new SPLResult();
        res.namaMetode = "Eliminasi Gauss-Jordan";
        
        int rows = aug.getRows();
        int cols = aug.getCols();
        int varCount = cols - 1;
        
        boolean recordSteps = rows <= 12;
        if(!recordSteps){
            res.langkah.add("Matriks besar, langkah detail tidak ditampilkan.");
        }
        
        Matrix temp = rref(aug, varCount, recordSteps ? res.langkah : null);
        
        for(int i=0; i<rows; i++){
            boolean allZero = true;
            for(int j=0; j<varCount; j++){
                if(Math.abs(temp.getElmt(i, j)) > 1e-9){
                    allZero = false;
                    break;
                }
            }
            if(allZero && Math.abs(temp.getElmt(i, cols-1)) > 1e-9){
                res.jenis = Jenis.NONE;
                return res;
            }
        }
        
        int[] pivotCols = new int[rows];
        for(int i=0; i<rows; i++){
            pivotCols[i] = -1;
            for(int j=0; j<varCount; j++){
                if(Math.abs(temp.getElmt(i, j)) > 1e-9){
                    pivotCols[i] = j;
                    break;
                }
            }
        }
        
        boolean[] isPivot = new boolean[varCount];
        for(int i=0; i<rows; i++){
            if(pivotCols[i] != -1){
                isPivot[pivotCols[i]] = true;
            }
        }
        
        boolean hasFree = false;
        res.namaParameter = new ArrayList<>();
        int paramIndex = 0;
        int[] varToParamIndex = new int[varCount];
        for(int c=0; c<varCount; c++){
            if(!isPivot[c]){
                hasFree = true;
                res.namaParameter.add(SPLResult.namaParameterKe(paramIndex));
                varToParamIndex[c] = paramIndex;
                paramIndex++;
            } else {
                varToParamIndex[c] = -1;
            }
        }
        
        res.jenis = hasFree ? Jenis.INFINITE : Jenis.UNIQUE;
        res.konstanta = new double[varCount];
        res.koefisienParam = new ArrayList<>();
        for(int i=0; i<varCount; i++){
            res.koefisienParam.add(new HashMap<>());
        }
        
        for(int c=0; c<varCount; c++){
            if(!isPivot[c]){
                res.konstanta[c] = 0.0;
                res.koefisienParam.get(c).put(res.namaParameter.get(varToParamIndex[c]), 1.0);
                continue;
            }
            
            int rowIdx = -1;
            for(int i=0; i<rows; i++){
                if(pivotCols[i] == c){
                    rowIdx = i;
                    break;
                }
            }
            
            double val = temp.getElmt(rowIdx, cols-1);
            if(Math.abs(val) < 1e-9) val = 0.0;
            res.konstanta[c] = val;
            
            Map<String, Double> map = res.koefisienParam.get(c);
            for(int j=c+1; j<varCount; j++){
                if(isPivot[j]) continue;
                double coeff = temp.getElmt(rowIdx, j);
                if(Math.abs(coeff) > 1e-9){
                    String pName = res.namaParameter.get(varToParamIndex[j]);
                    map.put(pName, -coeff);
                }
            }
        }
        
        return res;
    }

    public static SPLResult cramer(Matrix aug) {
        SPLResult res = new SPLResult();
        res.namaMetode = "Kaidah Cramer";
        
        int rows = aug.getRows();
        int cols = aug.getCols();
        int varCount = cols - 1;
        
        if(rows != varCount){
            res.jenis = Jenis.NONE;
            res.langkah.add("Kaidah Cramer hanya berlaku untuk SPL dengan jumlah persamaan sama dengan jumlah variabel.");
            return res;
        }
        
        Matrix A = new Matrix(rows, varCount);
        for(int i=0; i<rows; i++){
            for(int j=0; j<varCount; j++){
                A.setElmt(i, j, aug.getElmt(i, j));
            }
        }
        
        double detA = Determinan.cofactorExpansion(A);
        if(Math.abs(detA) < 1e-9){
            res.jenis = Jenis.NONE;
            res.langkah.add("Determinan A = 0, Kaidah Cramer tidak dapat digunakan.");
            return res;
        }
        
        res.jenis = Jenis.UNIQUE;
        res.konstanta = new double[varCount];
        
        for(int j=0; j<varCount; j++){
            Matrix Aj = A.copy();
            for(int i=0; i<rows; i++){
                Aj.setElmt(i, j, aug.getElmt(i, cols-1));
            }
            double detAj = Determinan.cofactorExpansion(Aj);
            res.langkah.add("det(A" + (j+1) + ") = " + IOHandler.formatNumber(detAj));
            
            double xj = detAj / detA;
            if(Math.abs(xj) < 1e-9) xj = 0.0;
            res.konstanta[j] = xj;
        }
        
        return res;
    }

    public static SPLResult inverseMethod(Matrix aug) {
        SPLResult res = new SPLResult();
        res.namaMetode = "Metode Matriks Balikan";
        
        int rows = aug.getRows();
        int cols = aug.getCols();
        int varCount = cols - 1;
        
        if(rows != varCount){
            res.jenis = Jenis.NONE;
            res.langkah.add("Metode matriks balikan hanya berlaku untuk SPL dengan jumlah persamaan sama dengan jumlah variabel.");
            return res;
        }
        
        Matrix A = new Matrix(rows, varCount);
        for(int i=0; i<rows; i++){
            for(int j=0; j<varCount; j++){
                A.setElmt(i, j, aug.getElmt(i, j));
            }
        }
        
        double detA = Determinan.rowReduction(A);
        if(Math.abs(detA) < 1e-9){
            res.jenis = Jenis.NONE;
            res.langkah.add("Matriks tidak memiliki balikan.");
            return res;
        }
        
        Matrix id = Matrix.createIdentityMatrix(rows);
        Matrix A_id = A.augment(id);
        
        Matrix rref_A_id = rref(A_id, varCount, null);
        
        Matrix invA = new Matrix(rows, rows);
        for(int i=0; i<rows; i++){
            for(int j=0; j<rows; j++){
                invA.setElmt(i, j, rref_A_id.getElmt(i, j + varCount));
            }
        }
        
        res.langkah.add("Berhasil menghitung A^-1 dengan RREF.");
        
        Matrix B = new Matrix(rows, 1);
        for(int i=0; i<rows; i++){
            B.setElmt(i, 0, aug.getElmt(i, cols-1));
        }
        
        Matrix X = invA.multiply(B);
        
        res.jenis = Jenis.UNIQUE;
        res.konstanta = new double[varCount];
        for(int i=0; i<varCount; i++){
            double val = X.getElmt(i, 0);
            if(Math.abs(val) < 1e-9) val = 0.0;
            res.konstanta[i] = val;
        }
        
        return res;
    }

    private static String matriksKeString(Matrix m) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getCols(); j++) {
                double v = m.getElmt(i, j);
                if (Math.abs(v) < 0.0005) v = 0;
                sb.append(String.format(java.util.Locale.US, "%9.3f", v));
            }
            if (i < m.getRows() - 1) sb.append("\n");
        }
        return sb.toString();
    }
}
