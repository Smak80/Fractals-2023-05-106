package drawing

class Plane (
    xMin: Double,
    xMax: Double,
    yMin: Double,
    yMax: Double,
    var width: Int,
    var height: Int
){
    private var _xMin: Double = xMin
    private var _xMax: Double = xMax
    private var _yMin: Double = yMin
    private var _yMax: Double = yMax

    var xMin: Double
        get() = _xMin - dx
        set(value){ _xMin = value }

    var xMax: Double
        get() = _xMax + dx
        set(value){ _xMax = value }

    var yMin: Double
        get() = _yMin - dy
        set(value){ _yMin = value }

    var yMax: Double
        get() = _yMax + dy
        set(value){ _yMax = value }

    private val dx: Double
        get() = if (OXY < OWH) xLen * (OWH / OXY - 1.0) / 2.0 else 0.0

    private val dy: Double
        get() = if (OXY > OWH) yLen * ((1.0 / OWH) / (1.0 / OXY) - 1.0) / 2.0 else 0.0

    private val OXY: Double
        get() = xLen / yLen

    private val OWH: Double
        get() = width.toDouble() / height.toDouble()

    private val xLen: Double
        get() = _xMax - _xMin

    private val yLen: Double
        get() = _yMax - _yMin

    val xSize: Double
        get() = xMax - xMin
    val ySize: Double
        get() = yMax - yMin
    val xDen: Double
        get() = width/(xMax-xMin)

    val yDen: Double
        get() = height/(yMax-yMin)
}