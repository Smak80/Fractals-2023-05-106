package ru.gr106.fractal.gui

import math.AlgebraicFractal
import math.Complex
import java.awt.Graphics
import drawing.Converter
import drawing.Plane
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.concurrent.thread
import kotlin.math.ln

class FractalPainter (val fractal: AlgebraicFractal) : Painter{

    var plane: Plane? = null
    override val width: Int
        get() = plane?.width?:0
    override val height: Int
        get() = plane?.height?:0
    var pointColor: (Float) -> Color = {if (it < 1f) Color.WHITE else Color.BLACK }
    var maxIteration: Int
        get() = fractal.maxIterations
        set(value) {fractal.maxIterations = value}


    override fun paint(g: Graphics) {
        val procCount = Runtime.getRuntime().availableProcessors()
        //как рисовать фрактал
        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        plane?.let{ plane ->
            val k: Double = ((plane.xMax - plane.xMin) * (plane.yMax - plane.yMin))
            val m: Double = 1.0 / k
            //if(dynamicItOn){
            if (k > 0.0001) maxIteration = 500
            if (k > 0.00000001 && k < 0.0001) maxIteration = ln(m / 100).toInt() * 200
            if (k < 0.00000001) maxIteration = ln(m / 1000000).toInt() * 200
            //  }
            Array(procCount){ thread {
                for (x in it..< width step procCount) {
                    for (y in 0..< height) {
                        val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                        img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                    }
                }
            }}.forEach { it.join() }
        }
        g.drawImage(img, 0, 0, null)

    }

}
