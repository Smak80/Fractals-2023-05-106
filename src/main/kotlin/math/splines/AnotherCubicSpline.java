package math.splines;

public class AnotherCubicSpline extends Spline {
    // here f' = 0 in points
    public AnotherCubicSpline(int N, double[] X, double[] y) {
        deg = 3;
        x = X.clone();
        n = N;
        g = new Polynom[N];
        Polynom ONE = Polynom.ONE;
        for (int i = 0; i < N; i++) {
            double hi = X[i + 1] - X[i];
            Polynom t = Polynom.linear(1.0 / hi, -X[i] / hi);
            Polynom phi1 = (ONE.sum(t.mult(-1))).pow(2);
            phi1 = phi1.mult(ONE.sum(t.mult(2)));
            Polynom phi2 = t.pow(2);
            phi2 = phi2.mult((ONE.mult(3)).sum(t.mult(-2)));
            g[i] = (phi1.mult(y[i])).sum(phi2.mult(y[i + 1]));
        }
    }
}
