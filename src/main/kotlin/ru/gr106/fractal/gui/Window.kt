package ru.gr106.fractal.gui

import math.*
import ru.smak.drawing.Converter
import ru.smak.drawing.Plane
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*


class Window : JFrame(){

    private val mainPanel: DrawingPanel
    private val fp: FractalPainter

    init{
        Mandelbrot.funcNum = 0//выбор функции -1 - жюлиа, 0,1,2,3 - мандельброт+функции

        Julia.c = Complex(-1.0, 0.0) // выбор точки Жюлиа; для теста: Julia.c = Complex(-0.2,0.75)

        fp = if (Mandelbrot.funcNum==-1) FractalPainter(Julia) else FractalPainter(Mandelbrot)

        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(600, 550)
        mainPanel = DrawingPanel(fp)
        createMenuBar() // создание меню

        mainPanel.addComponentListener(object : ComponentAdapter(){
            override fun componentResized(e: ComponentEvent?) {
                fp.plane?.width = mainPanel.width
                fp.plane?.height = mainPanel.height
                mainPanel.repaint()
            }
        })
        mainPanel.addSelectedListener {rect ->
            fp.plane?.let {
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
        fp.pointColor = SchemeChooser(2)    //выбор цветовой схемы - всего 3
    }
    private fun createMenuBar() {

        val menubar = JMenuBar()
        val file = JMenu("Файл")
        val  aMenuItem = JMenuItem("Сохранить")
        file.add(aMenuItem) // добавление новой ячейки в меню
        val  bMenuItem = JMenuItem("Отменить действие")
        file.add(bMenuItem)
        menubar.add(file)
        jMenuBar = menubar

        val file_color= JMenu("Выбор цветовой схемы")
        val  cMenuItem = JMenuItem("Синяя тема")
        file_color.add(cMenuItem)
        cMenuItem.addActionListener{ _: ActionEvent -> fp.pointColor = SchemeChooser(1)}
        val  dMenuItem = JMenuItem("Зеленая тема")
        dMenuItem.addActionListener{ _: ActionEvent -> fp.pointColor = SchemeChooser(2)}
        file_color.add(dMenuItem)
        val  eMenuItem = JMenuItem("Розовая тема")
        eMenuItem.addActionListener{ _: ActionEvent -> fp.pointColor = SchemeChooser(3)}
        file_color.add(eMenuItem)
        menubar.add(file_color)
        jMenuBar = menubar

        val file_ecs = JMenu("Экскурсия по фракталу")
        menubar.add(file_ecs)
        jMenuBar = menubar

        val file_j = JMenu("Запустить Жулиа")
        menubar.add(file_j)
        val  rectMenuItem = JMenuItem("Начать")
        file_j.add(rectMenuItem)
        rectMenuItem.addActionListener{ _:ActionEvent -> Mandelbrot.funcNum = -1  }
        rectMenuItem.addActionListener{ _:ActionEvent ->  Julia.c = Complex(-1.0, 0.0)}
        jMenuBar = menubar




    }
}