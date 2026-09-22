package algeo;

public class Invers {
    public static Matrix augmentInverse(Matrix m){
        if((m.getRows() != m.getCols()) || Math.abs(Determinan.rowReduction(m)) < 1e-9)
            return null;
        
        int size = m.getRows();
        Matrix iMatrix = Matrix.createIdentityMatrix(size);
        Matrix augmented = m.augment(iMatrix);

        //Fungsi OBE Gauss Jordan

        Matrix invers = new Matrix(size, size);
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                invers.setElmt(i, j, augmented.getElmt(i, j + size));

        return invers;

    }

    public static Matrix adjoinInverse(Matrix m){
        if((m.getRows() != m.getCols()) || Math.abs(Determinan.rowReduction(m)) < 1e-9)
            return null;

        int size = m.getRows();
        Matrix cofactor = new Matrix(size, size);
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                double minor = Determinan.cofactorExpansion(m.getCofactor(i, j));
                int sign;
                if((i+j) % 2 == 0) sign = 1;
                else sign = -1;

                cofactor.setElmt(i, j, (sign * minor));
                sign *= -1;
            }
        }

        double det = Determinan.cofactorExpansion(m);
        Matrix adjoin = cofactor.transpose();
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                adjoin.setElmt(i, j, (adjoin.getElmt(i, j) / det));

        return adjoin;
    }
}
