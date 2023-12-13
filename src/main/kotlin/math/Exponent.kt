package math

import kotlin.math.exp
import kotlin.math.ln

class Exponent {
    val C1: Double
    val C2: Double
    val T0: Double
    companion object {
        val FAKEE = Exponent(0.0, 0.0, 0.0)
    }

    // e^(C1*(T0 + t) + C2)
    constructor(c1: Double, c2: Double, t0: Double) {
        C1 = c1
        C2 = c2
        T0 = t0
    }

    constructor(c1: Double, t0: Double, t: Double, x: Double) {
        C1 = c1
        T0 = t0
        val tk = t + t0
        C2 = ln(x) - c1 * tk
    }

    // here t in [0, T]
    fun sb(t: Double): Double {
        return exp(C1 * (T0 + t) + C2)
    }

    // return t in [0, T]
    fun inverse(x: Double): Double {
        return (ln(x) - C2) / C1 - T0
    }
}