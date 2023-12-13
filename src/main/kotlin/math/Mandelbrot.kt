package math

import kotlin.math.exp

object Mandelbrot : AlgebraicFractal {
    val r = 2.0
    var function: (Complex) -> Complex = {value:Complex -> value }
    var r2 = r*r
    override var maxIterations: Int = 300

    override fun isInSet(z: Complex): Float {

        var i = 0
        val z1 = Complex()
        do{
            z1 *= function(z1)
            z1 += z
        } while(++i < maxIterations && z1.abs2() < r2)
        return i / maxIterations.toFloat()
    }

}