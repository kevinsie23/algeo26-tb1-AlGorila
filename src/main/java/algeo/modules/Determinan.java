package algeo.modules;

public class Determinan {
    public static double rowReduction(Matrix m){
        if(m.getRows() != m.getCols())
            return Double.NaN;

        Matrix temp = m.copy();

        int size = temp.getRows();
        int swap = 0;
        for(int i = 0; i < size; i++){
            int leading = i;
            for(int j = i+1; j < size; j++){
                if(Math.abs(temp.getElmt(j, i)) > Math.abs(temp.getElmt(leading, i)))
                    leading = j;
            }

            if(Math.abs(temp.getElmt(leading, i)) < 1e-9)
                    return 0;
            
            if(leading != i){
                temp.swapRows(i, leading);
                swap++;
            }

            for(int j = i+1; j < size; j++){
                double factor = -(temp.getElmt(j, i) / temp.getElmt(i, i));
                temp.addMulRow(j, i, factor);
            }
        }

        double det = 1;
        for(int i = 0; i < size; i++)
            det *= temp.getElmt(i, i);

        if(swap % 2 == 1)
            det *= -1;

        return det;
    }

    public static double cofactorExpansion(Matrix m){
        if(m.getRows() != m.getCols())
            return Double.NaN;

        double det = 0;
        int size = m.getRows();

        if(size == 1)
            return m.getElmt(0, 0);

        if(size == 2){
            det = (m.getElmt(0, 0) * m.getElmt(1, 1)) - (m.getElmt(0, 1) * m.getElmt(1, 0));
            return det;
        }

        int sign = 1;
        for(int i = 0; i < size; i++){
            double val = m.getElmt(0, i);
            if(Math.abs(val) < 1e-9){
                sign *= -1;
                continue;
            }

            Matrix sub = m.getCofactor(0, i);
            det += (sign * val * cofactorExpansion(sub));
            sign *= -1;
        }

        return det;
    }
}
