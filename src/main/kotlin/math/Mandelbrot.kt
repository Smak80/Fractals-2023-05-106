package math

import kotlin.math.log10
import kotlin.math.log2

object Mandelbrot : AlgebraicFractal {
    public final var funcNum: Int = 0

    override var maxIterations: Int = 500
    override fun isInSet(z: Complex): Float {
        var i = 0
        val z1 = Complex()


        funcNum.let {

            if(it==0) {
                val r = 2.0
                val r2 = r * r
                do {
                    z1 *= z1
                    z1 += z
                } while (++i < maxIterations && z1.abs2() < r2)
                return i / maxIterations.toFloat()
            }

            else if(it==1) {
                val r = 2.0
                val r2 = r * r
                val func1 = {do{
                    z1 *= Complex(z1.re*0.99-1, z1.im*0.99-1)
                    z1 += z
                } while(++i < maxIterations && z1.abs2() < r2)
                    i / maxIterations.toFloat()}
                return func1.invoke()
            }

            else if(it==2) {
                val func2 = {do{
                    val r = 4.0
                    val r2 = r * r
                    z1 *= z1
                    z1 += z
                } while(++i < maxIterations && z1.abs2() < r2)
                    i / maxIterations.toFloat()}
                return func2.invoke()
            }

            else {
                val func3 = {do{
                    val r = 1.0
                    val r2 = r * r
                    z1 *= z1
                    z1 += z
                } while(++i < maxIterations && z1.abs2() < r2)
                    i / maxIterations.toFloat()}
                return func3.invoke()
            }
        }
    }
}