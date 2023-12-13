package math.splines;

import java.lang.Math.*;

import static java.lang.Math.abs;

public class LinearSolve {

    //double[][] M1 = new double[n][n + 1];

    static void easy_gauss_jordan(double[][] M, int n) {
        for (int i0 = 0; i0 < n; ++i0) {
            double maxx = 0;
            int r = 0; // index of max row
            for (int i = i0; i < n; ++i) {
                if (abs(M[i][i0]) > maxx) {
                    maxx = abs(M[i][i0]);
                    r = i;
                }
            }
            //swap(M[i0], M[r]);
            double[] tmp = M[i0];
            M[i0] = M[r];
            M[r] = tmp;
            double am = M[i0][i0];
            for (int i = i0; i < n + 1; ++i) {
                M[i0][i] /= am;
            }
            for (int i = 0; i < n; ++i) {
                if (i != i0) {
                    double a = M[i][i0];
                    if (a != 0.0) {
                        for (int j = i0; j < n + 1; ++j) {
                            M[i][j] -= M[i0][j] * a;
                        }
                    }
                }
            }
        }
    }


    static void print(double[][] M) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[i].length; j++) {
                String padded = String.format("%.3f", M[i][j]);
                if (padded.charAt(0) != '-') {
                    padded = " " + padded;
                }
                str.append(padded + " ");
            }
            str.append("\n");
        }
        System.out.println(str);
    }


    static double[] tridiagonal(double a[], double b[], double c[], double f[], int n) {
        if (n == 0) {
            return new double[0];
        }
        if (n == 1) {
            double y[] = {f[0] / b[0]};
            return y;
        }
        double K[] = new double[n];
        double L[] = new double[n];
        double y[] = new double[n];
        K[1] = -c[0] / b[0];
        L[1] = f[0] / b[0];
        for (int i = 2; i < n; i++) {
            K[i] = -c[i - 1] / (a[i - 1] * K[i - 1] + b[i - 1]);
            L[i] = (-a[i - 1] * L[i - 1] + f[i - 1]) / (a[i - 1] * K[i - 1] + b[i - 1]);
        }
        y[n - 1] = (-a[n - 1] * L[n - 1] + f[n - 1]) / (a[n - 1] * K[n - 1] + b[n - 1]);
        for (int i = n - 2; i > -1; i--) {
            y[i] = K[i + 1] * y[i + 1] + L[i + 1];
        }
        return y;
    }
}
