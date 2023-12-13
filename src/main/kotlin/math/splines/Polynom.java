package math.splines;

public class Polynom {
    double[] ai;

    private static final double[] one = {1.0};
    private static final double[] zero = {0.0};
    public static final Polynom ONE = new Polynom(one);
    public static final Polynom ZERO = new Polynom(zero);

    Polynom(double[] coeff) {
        if (coeff != null) {
            ai = coeff.clone();
        } else {
            System.out.println("HEH MDA");
        }
    }

    Polynom(int n) {
        ai = new double[n];
    }

    private double[] mult(double[] A, double[] B) {
        double[] C = new double[A.length + B.length - 1];
        for (int i = 0; i < C.length; i++) {
            double v = 0;
            for (int j = 0; j <= i; j++) {
                if (j < A.length && i - j < B.length) {
                    v += A[j] * B[i - j];
                }
            }
            C[i] = v;
        }
        return C;
    }

    Polynom mult(Polynom p) {
        return new Polynom(mult(ai, p.ai));
    }

    private double[] sum(double[] A, double[] B) {
        double[] C = new double[Math.max(A.length, B.length)];
        for (int i = 0; i < A.length; i++) {
            C[i] = A[i];
        }
        for (int i = 0; i < B.length; i++) {
            C[i] += B[i];
        }
        return C;
    }

    Polynom sum(Polynom p) {
        return new Polynom(sum(ai, p.ai));
    }

    void sumAssign(Polynom p) {
        ai = sum(ai, p.ai);
    }

    Polynom add(double v) {
        double b[] = ai.clone();
        if (ai.length == 0) {
            b = new double[1];
        }
        b[0] += v;
        return new Polynom(b);
    }

    private double[] mult(double A[], double a) {
        double B[] = A.clone();
        for (int i = 0; i < B.length; i++) {
            B[i] *= a;
        }
        return B;
    }

    Polynom mult(double a) {
        return new Polynom(mult(ai, a));
    }

    private double[] divide(double A[], double a) {
        double B[] = A.clone();
        for (int i = 0; i < B.length; i++) {
            B[i] /= a;
        }
        return B;
    }

    Polynom divide(double a) {
        return new Polynom(divide(ai, a));
    }


    int degree() {
        return ai.length - 1;
    }

    Polynom diff() {
        Polynom p1;
        if (ai.length > 1) {
            p1 = new Polynom(ai.length - 1);
            int n = p1.ai.length;
            for (int i = 0; i < n; i++) {
                p1.ai[i] = ai[i + 1] * (i + 1);
            }
        } else {
            p1 = new Polynom(1);
        }
        return p1;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        double A[] = ai;
        if (A.length == 0) {
            return "ZERO";
        }
        if (A.length == 1) {
            return Double.toString(A[0]);
        }
        if (A[0] != 0) {
            str.append(A[0]);
        }
        for (int i = 1; i < A.length; i++) {
            if (A[i] != 0) {
                if (A[i] < 0) {
                    str.append(" " + A[i] + "*x^" + i);
                } else {
                    str.append(" + " + A[i] + "*x^" + i);
                }
            }
        }
        return str.toString();
    }

    double sb(double x) {
        double p[] = ai;
        double y = 0;
        int n = p.length;
        for (int i = n - 1; i > -1; i--) {
            y = y * x + p[i];
        }
        return y;
    }

    public static Polynom linear(double k, double b) {
        Polynom p = new Polynom(2);
        p.ai[0] = b;
        p.ai[1] = k;
        return p;
    }

    public static Polynom cubic(double x0, double x1, double x2, double x3) {
        Polynom p = new Polynom(4);
        double[] a = {x0, x1, x2, x3};
        p.ai = a;
        return p;
    }

    public static Polynom Const(double c) {
        Polynom p = new Polynom(1);
        p.ai[0] = c;
        return p;
    }

    public static Polynom pol2(double x0, double x1) {
        Polynom p = new Polynom(2);
        p.ai[0] = x0;
        p.ai[1] = x1;
        return p;
    }


    private Polynom pow(Polynom p, int n) {
        if (n == 0) {
            return ONE;
        }
        if (n == 1) {
            return p;
        }
        if (n % 2 == 0) {
            Polynom q = pow(p, n / 2);
            return q.mult(q);
        } else {
            return pow(p, n - 1).mult(p);
        }
    }

    Polynom pow(int n) {
        return pow(this, n);
    }

    double[] normalize(double a[]) {
        int n = a.length;
        int i = n - 1;
        while (a[i] == 0 && i > -1) {
            i--;
        }
        double b[] = new double[i + 1];
        for (int j = 0; j < b.length; j++) {
            b[j] = a[j];
        }
        return b;
    }

    Polynom normalize() {
        return new Polynom(normalize(ai));
    }

    boolean equal(Polynom P) {
        double EPS = 0.00000001;
        if (ai.length == P.ai.length) {
            for (int i = 0; i < ai.length; i++) {
                if (Math.abs(ai[i] - P.ai[i]) > EPS) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}