package algeo.modules;

import java.util.List;

public class Determinan {

    private static String f(double v){
        return IOHandler.formatNumber(v);
    }

    public static double rowReduction(Matrix m){
        return rowReduction(m, null);
    }

    public static double rowReduction(Matrix m, List<String> steps){
        if(m.getRows() != m.getCols())
            return Double.NaN;

        Matrix temp = m.copy();
        int size = temp.getRows();
        int swap = 0;

        if(steps != null){
            steps.add("Matriks awal:");
            steps.add(SPL.matriksKeString(temp));
        }

        for(int i = 0; i < size; i++){
            int leading = i;
            for(int j = i+1; j < size; j++){
                if(Math.abs(temp.getElmt(j, i)) > Math.abs(temp.getElmt(leading, i)))
                    leading = j;
            }

            if(Math.abs(temp.getElmt(leading, i)) < 1e-9){
                if(steps != null)
                    steps.add("\nKolom " + (i+1) + " tidak memiliki pivot, sehingga det(A) = 0");
                return 0;
            }
            
            if(leading != i){
                temp.swapRows(i, leading);
                swap++;
                if(steps != null){
                    steps.add("R" + (i+1) + " <-> R" + (leading+1) + " (tanda determinan dikali -1)");
                    steps.add(SPL.matriksKeString(temp));
                }
            }

            for(int j = i+1; j < size; j++){
                double factor = -(temp.getElmt(j, i) / temp.getElmt(i, i));
                temp.addMulRow(j, i, factor);
                if(steps != null && Math.abs(factor) > 1e-9){
                String sign = factor > 0 ? " + " : " - ";
                steps.add("R" + (j+1) + " <- R" + (j+1) + sign + f(Math.abs(factor)) + "*R" + (i+1));
                steps.add(SPL.matriksKeString(temp));
                }
            }
        }

        double det = 1;
        StringBuilder diag = new StringBuilder();
        for(int i = 0; i < size; i++){
            det *= temp.getElmt(i, i);
            if(steps != null && size <= 15){
                diag.append(f(temp.getElmt(i, i)));
                if(i < size-1)
                    diag.append(" x ");
            }
        }

        if(swap % 2 == 1)
            det *= -1;

        if(steps != null){
            steps.add("\nMatriks sudah berbentuk segitiga atas. Jumlah pertukaran baris = " + swap);
            if(size <= 15)
                steps.add("det(A) = " + ((swap % 2 == 1) ? "(-1) x " : "") + diag + " = " + f(det));
            else
                steps.add("det(A) = hasil kali diagonal utama" + ((swap % 2 == 1) ? " x (-1)" : "") + " = " + f(det));
        }


        return det;
    }

    public static double cofactorExpansion(Matrix m){
        return cofactorExpansion(m, null);
    }

    public static double cofactorExpansion(Matrix m, List<String> steps){
        if(m.getRows() != m.getCols())
            return Double.NaN;

        int size = m.getRows();

        if(size == 1){
            double v = m.getElmt(0, 0);
            if(steps != null)
                steps.add("det(A) = a11 = " + f(v));
            return v;
        }

        if(size == 2){
            double a = m.getElmt(0, 0), b = m.getElmt(0, 1);
            double c = m.getElmt(1, 0), d = m.getElmt(1, 1);
            double d2 = (a * d) - (b * c);
            if(steps != null){
                steps.add("Matriks 2x2: det(A) = a11*a22 - a12*a21");
                steps.add("det(A) = (" + f(a) + ")*(" + f(d) + ") - (" + f(b) + ")*(" + f(c) + ") = " + f(d2));
            }
            return d2;
        }

        if(steps != null){
            steps.add("Ekspansi kofaktor sepanjang baris 1 dari matriks:");
            steps.add(SPL.matriksKeString(m));
        }

        double det = 0;
        int sign = 1;
        for(int i = 0; i < size; i++){
            double val = m.getElmt(0, i);
            if(Math.abs(val) < 1e-9){
                if(steps != null)
                    steps.add("\nSuku ke-" + (i+1) + ": a1" + (i+1) + " = 0, suku bernilai 0 (dilewati)");
                sign *= -1;
                continue;
            }

            Matrix sub = m.getCofactor(0, i);
            double detSub = cofactorExpansion(sub);
            double suku = sign * val * detSub;
            det += suku;

            if(steps != null){
                String tanda = sign > 0 ? "+" : "-";
                steps.add("\nSuku ke-" + (i+1) + ": " + tanda + " a1" + (i+1) + " x det(M1" + (i+1) + ")");
                steps.add("Minor M1" + (i+1) + " =");
                steps.add(SPL.matriksKeString(sub));
                steps.add("det(M1" + (i+1) + ") = " + f(detSub));
                steps.add("Suku = " + tanda + " (" + f(val) + ") x (" + f(detSub) + ") = " + f(suku));
            }

            sign *= -1;
        }

        if(steps != null)
            steps.add("\ndet(A) = jumlah semua suku = " + f(det));

        return det;
    }
}
