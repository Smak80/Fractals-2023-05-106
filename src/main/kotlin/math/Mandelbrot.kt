package math

object Mandelbrot : AlgebraicFractal {
    val r = 50.0
    override var maxIterations: Int = 500
    override fun isInSet(z: Complex): Float {
        var i = 0
        val z1 = Complex()
        val r2 = r * r
        do{
            z1 *= z1
            z1 += z
        } while(++i < maxIterations && z1.abs2() < r2)
        return i / maxIterations.toFloat()
    }

}