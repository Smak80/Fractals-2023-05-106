package math.splines;

import java.util.Arrays;

public class LinearSpline extends Spline {
    public LinearSpline(int N, double X[], double y[]) {
        deg = 1;
        x = X.clone();
        n = N;
        g = new Polynom[N];
        for (int i = 0; i < N; i++) {
            g[i] = new Lagrange(2, Arrays.copyOfRange(X, i, i + 2), Arrays.copyOfRange(y, i, i + 2));
        }
    }
}