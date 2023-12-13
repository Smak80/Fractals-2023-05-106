package math.splines;

public class Lagrange extends Polynom {

    Lagrange(int n, double x[], double y[]) {
        super(n);

        for (int i = 0; i < n; i++) {
            // n - 1
            Polynom omega = Polynom.ONE;
            double alp = 1.0;
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    double[] t = {-x[j], 1.0};
                    Polynom q = new Polynom(t);
                    omega = omega.mult(q);
                    alp = alp * (x[i] - x[j]);
                }
            }
            omega = omega.divide(alp);
            sumAssign(omega.mult(y[i]));
            //System.out.print("l" + i + ":");
            //System.out.println(omega);
        }
    }
}
