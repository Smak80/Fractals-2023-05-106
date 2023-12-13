package drawing

data class Plane (
    var xMin: Double,
    var xMax: Double,
    var yMin: Double,
    var yMax: Double,
    var width: Int,
    var height: Int
){
    val xSize: Double
        get() = xMax - xMin
    val ySize: Double
        get() = yMax - yMin
    val xDen: Double
        get() = width/(xMax-xMin)

    val yDen: Double
        get() = height/(yMax-yMin)
    fun str(): String {
        return "<html>xMin=$xMin,<br>" +
                "xMax=$xMax,<br>" +
                "yMin=$yMin,<br>" +
                "yMax=$xMax,<br>" +
                "width=$width,<br>" +
                "height=$height</html>"
    }
}