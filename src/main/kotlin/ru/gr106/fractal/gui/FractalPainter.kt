package ru.gr106.fractal.gui

import math.AlgebraicFractal
import math.Complex
import java.awt.Graphics
import drawing.Converter
import drawing.Plane
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import kotlin.concurrent.thread
import kotlin.math.absoluteValue
import kotlin.math.ln

class FractalPainter (val fractal: AlgebraicFractal) : Painter{
    var previous_img: BufferedImage? = null
    var dx:Int = 0
    var dy:Int = 0
    val procCount = Runtime.getRuntime().availableProcessors()
    var DY : Boolean = false
    var plane: Plane? = null
    override val width: Int
        get() = plane?.width?:0
    override val height: Int
        get() = plane?.height?:0
    var pointColor: (Float) -> Color = {if (it < 1f) Color.WHITE else Color.BLACK }
    var maxIteration: Int
        get() = fractal.maxIterations
        set(value) { fractal.maxIterations = value }

    fun fullPaint(img:BufferedImage,rx:Int,ry:Int) : BufferedImage{


        //как рисовать фрактал

        plane?.let { plane ->
            Array(procCount) {
                thread {
                    if (rx == 0 && ry == 0) {
                        for (x in it..<width step procCount) {
                            for (y in 0..<height) {
                                val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                                img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                            }
                        }
                    }
                    if (ry < 0) {
                        for (y in height+ry+it..<height step procCount) {
                            for (x in 0..<width ) {
                                val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                                img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                            }
                        }
                    }
                    if (ry > 0) {
                        for (y in it..<ry step procCount) {
                            for (x in 0..<width ) {
                                val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                                img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                            }
                        }
                    }
                    if (rx < 0) {
                        for (x in width + rx + it..<width step procCount) {
                            for (y in 0..<height) {
                                val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                                img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                            }
                        }
                    }
                    if (rx > 0) {
                        for (x in it..<rx step procCount) {
                            for (y in 0..<height) {
                                val z = Complex(Converter.xScr2Crt(x, plane), Converter.yScr2Crt(y, plane))
                                img.setRGB(x, y, pointColor(fractal.isInSet(z)).rgb)
                            }
                        }
                    }
                }
            }.forEach { it.join() }
        }
        return img
    }

    override fun paint(g: Graphics) {

        var img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        if(DY) {
            var x : Double = 1.0
            plane?.let {
                x = 6 / ((it.xMax - it.xMin).absoluteValue * (it.yMax - it.yMin).absoluteValue)
            }
            if (x < 4) maxIteration = 300
            else if (x > 1000000000) maxIteration = 700 * ln(x).toInt()
            else if (x > 10000000) maxIteration = 500 * ln(x).toInt()
            else if (x > 100000) maxIteration = 300 * ln(x).toInt()
            else maxIteration = 300 + 50*ln(x).toInt()
        }else maxIteration = 300

        if (previous_img != null ) {
            img = fullPaint(img,dx,dy)
            img.graphics.drawImage(previous_img,dx,dy,null)
            previous_img = img


            g.drawImage(previous_img, 0, 0, null)
        }
        else{
            previous_img = fullPaint(img,0,0)
            g.drawImage(previous_img, 0, 0, null)
        }
    }

    fun copy(): FractalPainter {
        val fp = FractalPainter(fractal)
        fp.plane = plane
        fp.pointColor = pointColor
        return fp
    }
}
