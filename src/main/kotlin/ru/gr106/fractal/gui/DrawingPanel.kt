package ru.gr106.fractal.gui

import java.awt.Button
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JPanel

class DrawingPanel(val p:Painter) : JPanel() {
    private var rect = SelectionRect()
    private val selectedListener = mutableListOf<(SelectionRect)->Unit>()
    private var mouseButtonPressed = -1

    fun addSelectedListener(l: (SelectionRect)->Unit) {
        selectedListener.add(l)
    }

    fun removeSelectedListener(l: (SelectionRect)->Unit) {
        selectedListener.remove(l)
    }

    init {

        this.addMouseListener(object : MouseAdapter(){
            override fun mousePressed(e: MouseEvent?) {
                print(e?.button)
                if(e?.button==1) {
                    mouseButtonPressed = 1
                    e.let { e ->
                        rect = SelectionRect().apply {
                            addPoint(e.x, e.y)
                            graphics.apply {
                                setXORMode(Color.WHITE)
                                drawRect(-10, -10, 1, 1)
                                setPaintMode()
                            }
                        }
                    }
                }
            }

            override fun mouseReleased(e: MouseEvent?) {
                if (e?.button == 1) {
                    e?.let {
                        mouseButtonPressed = -1
                        if (rect.isCreated) drawRect()
                        rect.addPoint(it.x, it.y)
                        selectedListener.forEach { it(rect) }
                    }
                }
            }
        })
        this.addMouseMotionListener(object : MouseMotionAdapter(){
            override fun mouseDragged(e: MouseEvent?) {
                if (mouseButtonPressed == 1) {
                    e?.let {
                        if (rect.isCreated)
                            drawRect()
                        rect.addPoint(it.x, it.y)
                        drawRect()
                    }
                }
            }
        })
    }

    private fun drawRect() {
        graphics.apply{
            setXORMode(Color.WHITE)
            color = Color.BLACK
            drawRect(rect.x, rect.y, rect.width, rect.height)
            setPaintMode()
        }
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        g?.let{ p.paint(it) }
    }
}