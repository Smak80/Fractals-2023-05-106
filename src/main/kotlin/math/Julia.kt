package math

class Julia(cX: Double = -0.74543, cY: Double = 0.11300) : AlgebraicFractal {
    public var x: Double = cX
    public var y: Double = cY
    override var maxIterations: Int = 300
        set(value) { field = value.coerceIn(20..10000)}
    private var r = 2.0
    override fun isInSet(_z: Complex): Float {
        //+0.47 добавлено во время дебага для наглядности. Сдвиг фрактала налево. Надеюсь не забуду убрать :-)
        val z = Complex(_z.re + 0.47,_z.im)
        val c = Complex(x, y)
        val r2 = r*r
        for (i in 1..maxIterations){
            z*=z
            z+=c
            if (z.abs2() >= r2)
                return i.toFloat()/ maxIterations
        }
        return 1f
    }

}