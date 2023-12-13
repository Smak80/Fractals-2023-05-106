package ru.gr106.fractal.gui

import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JPanel

class DrawingPanel(val p:Painter) : JPanel() {
    private var rect = SelectionRect()
    private val selectedListener = mutableListOf<(SelectionRect)->Unit>()
    private var mouseButtonPressed = -1 //показывает какая кнопка мыши сейчас нажата: -1 - никакая, 1 - левая, 3 - правая; данная переменная была добавлена потому что в mousedragged e.button обнуляется.
    private var mouseButtonStartPointEndPoint = mutableListOf<Pair<Int, Int>>() //данный список хранит координаты нажатия и отпускания мыши(применяется при двигании фрактала)


    fun addSelectedListener(l: (SelectionRect)->Unit) {
        selectedListener.add(l)
    }

    fun removeSelectedListener(l: (SelectionRect)->Unit) {
        selectedListener.remove(l)
    }

    init {

        this.addMouseListener(object : MouseAdapter(){
            override fun mousePressed(e: MouseEvent?) {
                requestFocus()
                if(e?.button==1) {

                    rect.addPoint(0, 0)
                    rect.addPoint(width, height)
                    selectedListener.get(1).let{it(rect)}//запоминает состояние фрактала

                    mouseButtonPressed = 1

                    e.let {
                        rect = SelectionRect().apply {
                            addPoint(it.x, it.y)
                            graphics.apply {
                                setXORMode(Color.WHITE)
                                drawRect(-10, -10, 1, 1)
                                setPaintMode()
                            }
                        }
                    }
                } else if(e?.button==3){

                    mouseButtonPressed = 3                              
                    mouseButtonStartPointEndPoint.add(e.x to e.y)

                    rect.addPoint(0, 0)
                    rect.addPoint(width, height)
                    selectedListener.get(1).let{it(rect)}//запоминает состояние фрактала
                }
            }

            override fun mouseReleased(e: MouseEvent?) {
                if (mouseButtonPressed == 1) {
                    e?.let {

                        if (rect.isCreated) drawRect()
                        rect.addPoint(it.x, it.y)
                        selectedListener.get(0).let{ it(rect) }

                        rect.resetPoints() //обнуляем координаты точек, тк они не обнуляются
                        mouseButtonPressed = -1
                    }
                } else if (mouseButtonPressed == 3){
                    e?.let {
                        mouseButtonStartPointEndPoint.add(e.x to e.y)

                        rect.difX =
                            mouseButtonStartPointEndPoint.get(0).first.minus(mouseButtonStartPointEndPoint.get(0).first)
                        rect.difY =
                            mouseButtonStartPointEndPoint.get(0).second.minus(mouseButtonStartPointEndPoint.get(0).second)

                        selectedListener.get(0).let{it(rect)}//двигаем экран

                        mouseButtonPressed = -1

                        mouseButtonStartPointEndPoint.removeAt(1) //чистим наш список, чтобы при следующем двигании у нас был новый вектор движения экрана
                        mouseButtonStartPointEndPoint.removeAt(0)
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
                } else if(mouseButtonPressed == 3){
                    e?.let {
                        mouseButtonStartPointEndPoint.add(e.x to e.y)

                        rect.difX = mouseButtonStartPointEndPoint.get(1).first.minus(mouseButtonStartPointEndPoint.get(0).first)
                        rect.difY = mouseButtonStartPointEndPoint.get(1).second.minus(mouseButtonStartPointEndPoint.get(0).second)

                        selectedListener.get(0).let{it(rect)} //двигаем экран

                        mouseButtonStartPointEndPoint.removeAt(0) //убираем первую точку, чтобы в списке остался 1 элемент
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