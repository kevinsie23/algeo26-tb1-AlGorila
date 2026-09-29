package algeo.modules;

public class InterpolasiPolinomial {

    public static Matrix createAugmentedMatrix(double[][] points) {
        int n = points.length;
        Matrix aug = new Matrix(n, n + 1);
        for (int i = 0; i < n; i++) {
            double x = points[i][0];
            double y = points[i][1];
            for (int j = 0; j < n; j++) {
                aug.setElmt(i, j, Math.pow(x, j));
            }
            aug.setElmt(i, n, y);
        }

        return aug;
    }

  
    public static double evaluate(double[] koefisien, double xTarget) {
        double result = 0;
        for (int i = 0; i < koefisien.length; i++) {
            result += koefisien[i] * Math.pow(xTarget, i);
        }
        return result;
    }


    public static String getEquationString(double[] koefisien) {
        StringBuilder sb = new StringBuilder("P(x) = ");
        boolean isFirst = true;

        for (int i = 0; i < koefisien.length; i++) {
            double coef = koefisien[i];
            

            if (Math.abs(coef) < 1e-9) continue; 

 
            if (coef > 0 && !isFirst) {
                sb.append(" + ");
            } else if (coef < 0) {
                if (isFirst) sb.append("-");
                else sb.append(" - ");
            }


            sb.append(String.format("%.3f", Math.abs(coef)));

            if (i == 1) {
                sb.append("x");
            } else if (i > 1) {
                sb.append("x^").append(i);
            }
            
            isFirst = false;
        }
        
        return sb.toString();
    }
}