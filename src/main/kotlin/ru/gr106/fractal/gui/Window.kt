package ru.gr106.fractal.gui

import drawing.Converter
import drawing.Plane
import math.Mandelbrot
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.GroupLayout
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.*

class Window : JFrame() {

    private val mainPanel: DrawingPanel
    private val fp: FractalPainter

    init{
        fp = FractalPainter(Mandelbrot)
        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(600, 550)
        mainPanel = DrawingPanel(fp)

        mainPanel.addComponentListener(object : ComponentAdapter(){
            override fun componentResized(e: ComponentEvent?) {
                fp.plane?.width = mainPanel.width
                fp.plane?.height = mainPanel.height
                mainPanel.repaint()
            }
        })
        mainPanel.addSelectedListener {rect ->
            fp.plane?.let {
                val xMin = Converter.xScr2Crt(rect.x, it)
                val yMax = Converter.yScr2Crt(rect.y, it)
                val xMax = Converter.xScr2Crt(rect.x + rect.width, it)
                val yMin = Converter.yScr2Crt(rect.y + rect.height, it)
                it.xMin = xMin
                it.yMin = yMin
                it.xMax = xMax
                it.yMax = yMax
                mainPanel.repaint()
            }
        }
        mainPanel.background = Color.WHITE
        layout = GroupLayout(contentPane).apply {
            setVerticalGroup(
                createSequentialGroup()
                    .addGap(8)
                    .addComponent(mainPanel)
                    .addGap(8)
            )
            setHorizontalGroup(
                createSequentialGroup()
                    .addGap(8)
                    .addComponent(mainPanel)
                    .addGap(8)
            )
        }
        pack()
        fp.plane = Plane(-2.0, 1.0, -1.0, 1.0, mainPanel.width, mainPanel.height)
        fp.pointColor = {
            if (it == 1f) Color.BLACK else
            Color(
                0.5f*(1-cos(16f*it*it)).absoluteValue,
                sin(5f*it).absoluteValue,
                log10(1f + 5*it).absoluteValue
            )
        }
    }
}