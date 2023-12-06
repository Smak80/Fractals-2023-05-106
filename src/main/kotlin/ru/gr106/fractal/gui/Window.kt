package ru.gr106.fractal.gui

import math.*
import ru.smak.drawing.Converter
import ru.smak.drawing.Plane
import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.*
import java.beans.PropertyChangeListener
import javax.swing.*

class Window : JFrame(){

    private val mainPanel: DrawingPanel
    private val fp: FractalPainter

    private var stateList = mutableListOf<State>() //список состояний(для отмены действий)
    private var colorScheme = 1 //хранит в себе цветовую схему

    init{
        Mandelbrot.funcNum = -1 //выбор функции -1 - жюлиа, 0,1,2,3 - мандельброт+функции

        Julia.c = Complex(-0.5,0.75)// выбор точки Жюлиа; для теста: Julia.c = Complex(-0.2,0.75)

        fp = if (Mandelbrot.funcNum==-1) FractalPainter(Julia) else FractalPainter(Mandelbrot)

        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(600, 550)
        mainPanel = DrawingPanel(fp)
        createMenuBar() // создание меню

        /*val rollback: Action = object : Action {
            override fun actionPerformed(e: ActionEvent?) {
                println("some")
                val rect = SelectionRect()
                rect.addPoint(stateList.last.xMin.toInt(), stateList.last.xMin.toInt(), stateList.last.xMin.toInt(), stateList.last.xMin.toInt())

                mainPanel.selectedListener.forEach{it(rect)}
            }

            override fun getValue(key: String?): Any {
                TODO("Not yet implemented")
            }

            override fun putValue(key: String?, value: Any?) {
                TODO("Not yet implemented")
            }

            override fun setEnabled(b: Boolean) {
                TODO("Not yet implemented")
            }

            override fun isEnabled(): Boolean {
                TODO("Not yet implemented")
            }

            override fun addPropertyChangeListener(listener: PropertyChangeListener?) {
                TODO("Not yet implemented")
            }

            override fun removePropertyChangeListener(listener: PropertyChangeListener?) {
                TODO("Not yet implemented")
            }
        }*/

        mainPanel.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "pressed")

        //mainPanel.actionMap.put("pressed", rollback)

        mainPanel.addComponentListener(object : ComponentAdapter(){
            override fun componentResized(e: ComponentEvent?) {
                fp.plane?.width = mainPanel.width
                fp.plane?.height = mainPanel.height
                mainPanel.repaint()
            }
        })
        mainPanel.addKeyListener(object : KeyAdapter(){
            override fun keyReleased(e: KeyEvent?) {
                if (e != null) {
                    println("e is not null")
                    if (e.isControlDown){
                        println("control is down")
                        /*val rect = SelectionRect()
                        rect.addPoint(stateList.last.xMin.toInt(), stateList.last.xMin.toInt(), stateList.last.xMin.toInt(), stateList.last.xMin.toInt())

                        mainPanel.selectedListener.forEach{it(rect)}*/

                        fp.plane?.let {
                            it.xMin = stateList.last.xMin
                            it.yMin = stateList.last.yMin
                            it.xMax = stateList.last.xMax
                            it.yMax = stateList.last.yMax
                            mainPanel.repaint()
                        }

                    }
                }
            }
        })
        mainPanel.addSelectedListener {rect ->
            fp.plane?.let {

                val someState = State(Mandelbrot.funcNum, it.xMin, it.xMax, it.yMin, it.yMax, colorScheme, Julia.c)
                stateList.add(someState)//добавление состояния в список состояний

                val xMin = Converter.xScr2Crt(rect.x - rect.difX, it)
                val yMax = Converter.yScr2Crt(rect.y- rect.difY, it)
                val xMax = Converter.xScr2Crt(rect.x + rect.width -  rect.difX, it)
                val yMin = Converter.yScr2Crt(rect.y + rect.height- rect.difY, it)
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
        fp.pointColor = SchemeChooser(colorScheme)    //выбор цветовой схемы - всего 3
    }
    private fun createMenuBar() {

        val menubar = JMenuBar()
        val file = JMenu("Файл")
        val  eMenuItem = JMenuItem("Сохранить")
        file.add(eMenuItem) // добавление новой ячейки в меню
        menubar.add(file)
        jMenuBar = menubar
    }

    fun addState(state: State){
        stateList.add(state)
    }
}

data class State(val fractal: Int, val xMin: Double, val xMax: Double, val yMin: Double, val yMax: Double, val colorScheme: Int, val pointJulia: Complex?) {
}