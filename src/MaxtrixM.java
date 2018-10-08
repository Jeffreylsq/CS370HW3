import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class MaxtrixM {
    public static void main(String[] args) throws InterruptedException{
        
    	Phaser ph = new Phaser();
        float[][] matrixA, matrixB, matrixC;
        int a, b, p;

        a = b = p = 1024;
        matrixA = fillMatrixWith1(a, b);
        matrixB = fillMatrixWith1(b, p);

        long timeIn, timeOut;
        int sleepTime = 2000;

        // 1 Thread
        matrixC = new float[a][p];
        timeIn = System.nanoTime();

        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, 0, a, b, 0, p);

        Thread.sleep(sleepTime);
        timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 1 Threads: %5.10f sec\n", (timeOut / 1e9));

        // 2 Threads
        matrixC = new float[a][p];
        timeIn = System.nanoTime();

        // Quad 1 & 3
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, 0, a, b, 0, p / 2);
        // Quad 2 & 4
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, 0, a, b, p / 2, p);

        Thread.sleep(sleepTime);
        timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 2 Threads: %5.10f sec\n", (timeOut / 1e9));

        // 4 Threads
        matrixC = new float[a][p];
        timeIn = System.nanoTime();

        // Quad 1
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, 0, a / 2, b, 0, p / 2);
        // Quad 2
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, 0, a / 2, b, p / 2, p);
        // Quad 3
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, a / 2, a, b, 0, p / 2);
        // Quad 4
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, a / 2, a, b, p / 2, p);

        Thread.sleep(sleepTime);
        timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 4 Threads: %5.10f sec\n", (timeOut / 1e9));

        // 8 Threads
        matrixC = new float[a][p];
        timeIn = System.nanoTime();

        // Quad 1
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, 0, a / 4, b, 0, p / 2);
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, a / 4, a / 2, b, 0, p / 2);
        // Quad 2
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, a / 4, a / 2, b, p / 2, p);
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, 0, a / 4, b, p / 2, p);
        // Quad 3
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, a / 2, a * 3 / 4, b, 0, p / 2);
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, a / 2, a * 3 / 4, b, p / 2, p);
        //Quad 4
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, a * 3 / 4, a, b, 0, p / 2);
        new MaxtrixM().matMult(ph, matrixA, matrixB, matrixC, a / 2, a, b, p / 2, p);

        Thread.sleep(sleepTime);
        timeOut = System.nanoTime() - timeIn;
        System.out.printf("Time with 8 Threads: %5.10f sec\n", (timeOut / 1e9));
    }

    public void matMult(Phaser ph, float[][] A, float[][] B, float[][] C, int m1, int m2, int n, int p1, int p2) {
        ph.register();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int row = m1; row < m2; row++) {
                    for (int col = p1; col < p2; col++) {
                        C[row][col] = 0;
                        for (int k = 0; k < n; k++) {
                            C[row][col] += A[row][k] * B[k][col];
                        }
                    }
                }
                ph.arriveAndDeregister();
            }
        }).start();
    }

    public static float[][] fillMatrix(int row, int col) {
        float[][] temp = new float[row][col];

        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                temp[i][j] = ThreadLocalRandom.current().nextFloat();
            }
        }
        return temp;
    }

    public static float[][] fillMatrixWith1(int row, int col) {
        float[][] temp = new float[row][col];

        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                temp[i][j] = 1;
            }
        }
        return temp;
    }

    public static void print (float[][] m) {
        for (int row = 0; row < m.length; row++) {
            System.out.print("[");
            for (int col = 0; col < m.length; col++) {
                if (col == m.length - 1)
                    System.out.printf("%-5.2f", m[row][col]);
                else
                    System.out.printf("%-5.2f", m[row][col]);
            }
            System.out.println("]");
        }
        System.out.println();
    }
}