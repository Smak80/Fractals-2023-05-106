package drawing

object Converter {

    fun xCrt2Scr(x: Double, p: Plane): Int = (p.xDen * (x - p.xMin)).coerceIn(-p.width.toDouble(),2 * p.width.toDouble()).toInt()
    fun yCrt2Scr(y: Double, p: Plane): Int = (p.yDen * (p.yMax - y)).coerceIn(-p.height.toDouble(),2 * p.height.toDouble()).toInt()

    fun xScr2Crt(x: Int, p: Plane): Double = x.toDouble() / p.xDen + p.xMin
    fun yScr2Crt(y: Int, p: Plane): Double = p.yMax - y.toDouble() / p.yDen
}