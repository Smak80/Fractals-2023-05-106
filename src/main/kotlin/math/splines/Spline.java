package math.splines;

public class Spline {
    int deg = 1;
    double x[];
    int n;
    Polynom g[];

    // m - deg of spline
    // n - number of segments
    // size of X = n + 1
    // size of y = n + 1
    public Spline() {
    }

    public double sb(double X) {
        int i = 0;
        while (i + 1 < n && x[i + 1] < X) {
            i++;
        }
        return g[i].sb(X);
    }

    void checkSpline() {
        System.out.println("--------- Check Spline ---------");
        for (int i = 1; i < n; i++) {
            double X = x[i];
            System.out.println(g[i - 1].sb(X) + " " + g[i].sb(X));
        }
        if (deg == 3) {
            for (int i = 1; i < n; i++) {
                double X = x[i];
                Polynom g1 = g[i - 1].diff();
                Polynom g2 = g[i].diff();
                double df = Math.abs(g1.sb(X) - g2.sb(X));
                System.out.println(df);
                if (df > 0.0001) {
                    System.out.println("ERROR");
                }
                Polynom g11 = g1.diff();
                Polynom g22 = g2.diff();
                df = Math.abs(g11.sb(X) - g22.sb(X));
                System.out.println(df);
                if (df > 0.0001) {
                    System.out.println("ERROR");
                }
            }
            System.out.println("g''_0(x_0): " + g[0].diff().diff().sb(x[0]));
            System.out.println("g''_n(x_n): " + g[n - 1].diff().diff().sb(x[n]));
        }
        System.out.println("----------------------");
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < n; i++) {
            str.append("g" + (i + 1) + ": " + g[i] + "\n");
        }
        return str.toString();
    }

    boolean equal(double a[], double b[]) {
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    boolean isEqual(Spline S) {
        if (deg == S.deg && n == S.n && equal(S.x, x)) {
            for (int i = 0; i < n; i++) {
                if (!g[i].equal(S.g[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
