package algeo.modules;

public class RegresiSpline {


    private static double truncatedPower(double x, double knot) {
        double diff = x - knot;
        if (diff > 0) {
            return Math.pow(diff, 3);
        }
        return 0.0;
    }

    public static Matrix createDesignMatrix(double[][] points, double[] knots) {
        int n = points.length;
        int k = knots.length;
        int m = 4 + k; 
        Matrix X = new Matrix(n, m);

        for (int i = 0; i < n; i++) {
            double x = points[i][0];
            
 
            X.setElmt(i, 0, 1.0);
            X.setElmt(i, 1, x);
            X.setElmt(i, 2, Math.pow(x, 2));
            X.setElmt(i, 3, Math.pow(x, 3));
            

            for (int j = 0; j < k; j++) {
                X.setElmt(i, 4 + j, truncatedPower(x, knots[j]));
            }
        }
        return X;
    }

    public static Matrix createYMatrix(double[][] points) {
        int n = points.length;
        Matrix Y = new Matrix(n, 1);
        for (int i = 0; i < n; i++) {
            Y.setElmt(i, 0, points[i][1]);
        }
        return Y;
    }

    public static Matrix calculateBeta(Matrix X, Matrix Y) {
        Matrix XT = X.transpose();
        

        Matrix XTX = XT.multiply(X);

        Matrix XTY = XT.multiply(Y);

   
        Matrix XTX_inv = Invers.augmentInverse(XTX); 
        
   
        if (XTX_inv == null) {
            return null; 
        }


        Matrix beta = XTX_inv.multiply(XTY);
        return beta;
    }


    public static double evaluate(Matrix beta, double[] knots, double xTarget) {
        double result = 0.0;
        
        result += beta.getElmt(0, 0) * 1.0;
        result += beta.getElmt(1, 0) * xTarget;
        result += beta.getElmt(2, 0) * Math.pow(xTarget, 2);
        result += beta.getElmt(3, 0) * Math.pow(xTarget, 3);
        
        for (int j = 0; j < knots.length; j++) {
            result += beta.getElmt(4 + j, 0) * truncatedPower(xTarget, knots[j]);
        }
        
        return result;
    }
}