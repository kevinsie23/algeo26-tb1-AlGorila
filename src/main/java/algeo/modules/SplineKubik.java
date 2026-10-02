package algeo.modules;

public class SplineKubik {

    public static Matrix createTridiagonalMatrix(double[][] points) {
        int n = points.length;
        Matrix aug = new Matrix(n, n + 1);

        aug.setElmt(0, 0, 1.0);
        aug.setElmt(0, n, 0.0);

        aug.setElmt(n - 1, n - 1, 1.0);
        aug.setElmt(n - 1, n, 0.0);


        for (int i = 1; i < n - 1; i++) {
            double hMin1 = points[i][0] - points[i - 1][0]; 
            double hI = points[i + 1][0] - points[i][0];   

         
            aug.setElmt(i, i - 1, hMin1);                 
            aug.setElmt(i, i, 2 * (hMin1 + hI));           
            aug.setElmt(i, i + 1, hI);                     

        
            double yMin1 = points[i - 1][1];
            double yI = points[i][1];
            double yPlus1 = points[i + 1][1];
            
            double rhs = 6 * (((yPlus1 - yI) / hI) - ((yI - yMin1) / hMin1));
            aug.setElmt(i, n, rhs);
        }

        return aug;
    }
}