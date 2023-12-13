package math.splines;

// Natural Spline
// Creation by moments algorithm
public class CubicMomentSpline extends Spline {

    // Natural (esstestvennyi) Spline
    // Creation by moments algo
    public CubicMomentSpline(int N, double X[], double y[]) {
        deg = 3;
        x = X.clone();
        n = N;
        g = new Polynom[N];
        double dx[] = new double[n + 1];
        for (int k = 1; k < n + 1; k++) {
            dx[k] = x[k] - x[k - 1];
        }
        double a[] = new double[n - 1];
        double b[] = new double[n - 1];
        double c[] = new double[n - 1];
        double B[] = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            int k = i + 1;
            b[i] = 2 * (dx[k] + dx[k + 1]);
        }
        for (int i = 1; i < n - 1; i++) {
            int k = i + 1;
            a[i] = dx[k];
        }
        for (int i = 0; i < n - 2; i++) {
            int k = i + 1;
            c[i] = dx[k + 1];
        }
        for (int i = 0; i < n - 1; i++) {
            int k = i + 1;
            B[i] = (y[k + 1] - y[k]) / dx[k + 1] - (y[k] - y[k - 1]) / dx[k];
            B[i] = 6 * B[i];
        }
        // moments
        double M1[] = LinearSolve.tridiagonal(a, b, c, B, n - 1);
        double M[] = new double[n + 1];
        for (int i = 0; i < M1.length; i++) {
            M[i + 1] = M1[i];
        }
        double g1[] = new double[n + 1];
        for (int k = 1; k < n + 1; k++) {
            g1[k] = (y[k] - y[k - 1]) / dx[k] + (dx[k] / 6.0) * M[k - 1] + (dx[k] / 3.0) * M[k];
        }
        g1[0] = (y[1] - y[0]) / dx[1] - (dx[1] / 3.0) * M[0] - (dx[1] / 6.0) * M[1];

        for (int i = 0; i < n; i++) {
            Polynom t = Polynom.linear(1.0 / dx[i + 1], -x[i] / dx[i + 1]);
            Polynom s0 = Polynom.Const(y[i]);
            Polynom s1 = t.mult(dx[i + 1]).mult(g1[i]);
            Polynom s2 = (t.mult(3.0)).sum((t.mult(-1).add(1)).pow(3)).add(-1);
            s2 = s2.mult(M[i]).mult((dx[i + 1] * dx[i + 1]) / 6.0);
            Polynom s3 = (t.pow(3)).mult(M[i + 1]).mult((dx[i + 1] * dx[i + 1]) / 6.0);
            g[i] = s0.sum(s1).sum(s2).sum(s3);
        }
    }

}