package algeo.modules;

public class Matrix {
    private double[][] data;
    private int rows;
    private int cols;


    public Matrix(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }


    public int getRows(){
        return this.rows;
    }

    public int getCols(){
        return this.cols;
    }

    public double getElmt(int i, int j){
        return this.data[i][j];
    }


    public void setElmt(int i, int j, double val){
        this.data[i][j] = val;
    }


    public void swapRows(int row1, int row2){
        double[] temp = this.data[row1];
        this.data[row1] = this.data[row2];
        this.data[row2] = temp;
    }

    public void multiplyRow(int row, double val){
        for(int i = 0; i < this.cols; i++)
            this.data[row][i] *= val;
    }

    public void addMulRow(int row1, int row2, double val){
        for(int i = 0; i < this.cols; i++)
            this.data[row1][i] += (this.data[row2][i] * val);
    }


    public void add(Matrix other){
        if(this.rows != other.rows || this.cols != other.cols)
            throw new IllegalArgumentException("Ukuran kedua matriks harus sama!");

        for(int i = 0; i < this.rows; i++)
            for(int j = 0; j < this.cols; j++)
                this.data[i][j] += other.data[i][j];
    }

    public void subtract(Matrix other){
        if(this.rows != other.rows || this.cols != other.cols)
            throw new IllegalArgumentException("Ukuran kedua matriks harus sama!");


        for(int i = 0; i < this.rows; i++)
            for(int j = 0; j < this.cols; j++)
                this.data[i][j] -= other.data[i][j];
    }

    public Matrix multiply(Matrix other){
        if(this.cols != other.rows)
            throw new IllegalArgumentException("Ukuran kedua matriks harus sama!");


        Matrix result = new Matrix(this.rows, other.cols);

        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < other.cols; j++){
                result.data[i][j] = 0;
                for(int k = 0; k < this.cols; k++)
                    result.data[i][j] += this.data[i][k] * other.data[k][j];
            }
        }

        return result;
    }

    public Matrix transpose(){
        Matrix transposed = new Matrix(this.cols, this.rows);
        
        for(int i = 0; i < this.cols; i++)
            for(int j = 0; j < this.rows; j++)
                transposed.data[i][j] = this.data[j][i];

        return transposed;
    }

    public Matrix getCofactor(int iCof, int jCof){
        if(this.rows != this.cols)
            return null;

        if(this.rows == 1)
            return this;

        Matrix cofactor = new Matrix(this.rows-1, this.cols-1);
        int newI = 0, newJ = 0;
        for(int i = 0; i < this.rows; i++){
            if(i == iCof)
                continue;

            newJ = 0;
            for(int j = 0; j < this.cols; j++){
                if(j == jCof)
                    continue;

                cofactor.data[newI][newJ] = this.data[i][j];
                newJ++;
            }
            newI++;
        }

        return cofactor;
    }

    public Matrix augment(Matrix other){
        if(this.rows != other.rows)
            throw new IllegalArgumentException("Jumlah baris kedua matriks harus sama!");

        int newCols = this.cols + other.cols;
        Matrix augmented = new Matrix(this.rows, newCols);
        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < newCols; j++){
                if(j < this.cols)
                    augmented.data[i][j] = this.data[i][j];

                if(j >= this.cols)
                    augmented.data[i][j] = other.data[i][j - this.cols];
            }
        }

        return augmented;
    }

    public Matrix copy(){
        Matrix copyMatrix = new Matrix(this.rows, this.cols);
        
        for(int i = 0; i < this.rows; i++)
            for(int j = 0; j < this.cols; j++)
                copyMatrix.data[i][j] = this.data[i][j];
        
        return copyMatrix;
    }

    public static Matrix createIdentityMatrix(int n){
        Matrix identity = new Matrix(n, n);
        for(int i = 0; i < n; i++)
                identity.setElmt(i, i, 1);

        return identity;
    }
}
