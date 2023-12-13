package ru.gr106.fractal.gui

import math.AlgebraicFractal
import math.Complex
import java.awt.Graphics
import drawing.Converter
import drawing.Plane
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.concurrent.thread

class FractalPainter (val fractal: AlgebraicFractal) : Painter{

    var plane: Plane? = null
    override val width: Int
        get() = plane?.width?:0
    override val height: Int
        get() = plane?.height?:0
    var pointColor: (Float) -> Color = {if (it < 1f) Color.WHITE else Color.BLACK }


    override fun paint(g: Graphics) {
        val procCount = Runtime.getRuntime().availableProcessors()
        //как рисовать фрактал
        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        plane?.let{ plane ->
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
