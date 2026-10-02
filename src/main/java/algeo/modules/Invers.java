package algeo.modules;

import java.util.List;

public class Invers {
    private static String f(double v){
        return IOHandler.formatNumber(v);
    }

    public static Matrix augmentInverse(Matrix m){
        return augmentInverse(m, null);        
    }

    public static Matrix augmentInverse(Matrix m, List<String> steps){
        if(m.getRows() != m.getCols())
            return null;
        
        int size = m.getRows();
        if(Math.abs(Determinan.rowReduction(m)) < 1e-9){
            if(steps != null)
                steps.add("det(A) = 0, matriks tidak memiliki invers.");
            return null;
        }

        Matrix augmented = m.augment(Matrix.createIdentityMatrix(size));
        if(steps != null){
            steps.add("Matriks augmentasi [A | I]:");
            steps.add(SPL.matriksKeString(augmented));
        }

        augmented = SPL.rref(augmented, size, steps);

        Matrix invers = new Matrix(size, size);
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                invers.setElmt(i, j, augmented.getElmt(i, j + size));

        if(steps != null){
            steps.add("\nBentuk akhir [I | A^-1], sehingga A^-1 =");
            steps.add(SPL.matriksKeString(invers));
        }

        return invers;

    }



    public static Matrix adjoinInverse(Matrix m){
            return null;
    }

    public static Matrix adjoinInverse(Matrix m, List<String> steps){
        if(m.getRows() != m.getCols())
            return null;

        int size = m.getRows();
        if(Math.abs(Determinan.rowReduction(m)) < 1e-9){
            if(steps != null)
                steps.add("det(A) = 0, matriks tidak memiliki invers.");
            return null;
        }

        if(size == 1){
            Matrix inv1 = new Matrix(1, 1);
            inv1.setElmt(0, 0, 1.0 / m.getElmt(0, 0));
            if(steps != null)
                steps.add("Matriks 1x1: A^-1 = 1 / " + f(m.getElmt(0, 0)) + " = " + f(inv1.getElmt(0, 0)));
            return inv1;
        }

        if(steps != null){
            steps.add("Matriks A:");
            steps.add(SPL.matriksKeString(m));
        }

        Matrix cofactor = new Matrix(size, size);
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                double minor = Determinan.cofactorExpansion(m.getCofactor(i, j));
                int sign = ((i + j) % 2 == 0) ? 1 : -1;
                cofactor.setElmt(i, j, sign * minor);
            }
        }

        if(steps != null){
            steps.add("\nMatriks kofaktor C, dengan C_ij = (-1)^(i+j) x det(M_ij):");
            steps.add(SPL.matriksKeString(cofactor));
        }

        double det = Determinan.cofactorExpansion(m);
        if(steps != null)
            steps.add("\ndet(A) = " + f(det));
        
        Matrix adjoin = cofactor.transpose();
        if(steps != null){
            steps.add("\nAdjoin(A) = transpos dari matriks kofaktor:");
            steps.add(SPL.matriksKeString(adjoin));
        }


        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                adjoin.setElmt(i, j, (adjoin.getElmt(i, j) / det));

        if(steps != null){
            steps.add("\nA^-1 = (1 / " + f(det) + ") x Adjoin(A) =");
            steps.add(SPL.matriksKeString(adjoin));
        }

        return adjoin;
    }
}
