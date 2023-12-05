package math

import ru.gr106.fractal.gui.DrawingPanel
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.sqrt

object Julia : AlgebraicFractal {
    public final lateinit var c: Complex
    override var maxIterations: Int = 500
    override fun isInSet(z: Complex): Float {
        c.let {
            var i = 0
            val z1 = z
            //val c = Complex(-1.0, 0.0)
            do {
                z1 *= z1
                z1 += c
            } while (++i < maxIterations && z1.abs2() < 2)
            return i / maxIterations.toFloat()
        }
    }
}