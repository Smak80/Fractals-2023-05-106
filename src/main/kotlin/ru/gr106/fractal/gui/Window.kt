package ru.gr106.fractal.gui

import math.*
import ru.smak.drawing.Converter
import ru.smak.drawing.Plane
import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.*
import java.beans.PropertyChangeListener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*


class Window : JFrame(){

    private val mainPanel: DrawingPanel
    private lateinit var juliaPanel: DrawingPanel
    private val fp: FractalPainter
    private lateinit var ju: FractalPainter



    private var stateList = mutableListOf<State>() //список состояний(для отмены действий)
    private var colorScheme = 1 //хранит в себе цветовую схему
    private var newWidth: Int = 0
    private var newHeight: Int = 0
    private var dx: Double = 0.0
    private var dy: Double = 0.0
    private var yMin = -1.0
    private var yMax = 1.0
    private var xMin = -2.0
    private var xMax = 1.0

    init{
        Mandelbrot.funcNum = 0 //выбор функции -1 - жюлиа, 0,1,2,3 - мандельброт+функции

        Julia.c = Complex(-0.5,0.75)// выбор точки Жюлиа; для теста: Julia.c = Complex(-0.2,0.75)

        fp = if (Mandelbrot.funcNum==-1) FractalPainter(Julia) else FractalPainter(Mandelbrot)

        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(600, 550)
        mainPanel = DrawingPanel(fp)
        createMenuBar() // создание меню


        mainPanel.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "pressed")

        mainPanel.addComponentListener(object : ComponentAdapter(){
            override fun componentResized(e: ComponentEvent?) {
                fp.plane?.width = mainPanel.width
                fp.plane?.height = mainPanel.height
                newHeight = mainPanel.height
                newWidth = mainPanel.width

                val OXlength = xMax - xMin
                val OYlength = yMax - yMin

                var newXMax = xMax
                var newXMin = xMin
                var newYMax = yMax
                var newYMin = yMin

                val relationOXY = OXlength * 1.0 / OYlength

                val relationWidthHeight = newWidth * 1.0 / newHeight

                if (Math.abs(relationOXY - relationWidthHeight) > 1E-5){
                    if (relationOXY < relationWidthHeight){
                        dx = relationWidthHeight - relationOXY
                        newXMin -= dx
                        newXMax += dx
                    }
                    if (relationOXY > relationWidthHeight){
                        dy = relationOXY - relationWidthHeight
                        newYMin -= dy
                        newYMax += dy
                    }
                }
                val xPair = Pair(newXMin, newXMax)
                val yPair = Pair(newYMin, newYMax)
                val mapOfCoord = mutableMapOf<Pair<Double, Double>, Pair<Double, Double>>()
                mapOfCoord.put(xPair, yPair)
                fp.plane?.xMax = newXMax
                fp.plane?.xMin = newXMin
                fp.plane?.yMin = newYMin
                fp.plane?.yMax = newYMax
                fp.plane?.height = mainPanel.height
                fp.plane?.width = mainPanel.width

                //fp.previousImage = null
                mainPanel.repaint()
            }
        })
        mainPanel.addKeyListener(object : KeyAdapter(){
            override fun keyReleased(e: KeyEvent?) {
                if (e != null && e.isControlDown) {
                    fp.plane?.let {
                        if(stateList.size != 0){
                            fp.pointColor = SchemeChooser(stateList.last().colorScheme)
                            it.xMin = stateList.last().xMin
                            it.yMin = stateList.last().yMin
                            it.xMax = stateList.last().xMax
                            it.yMax = stateList.last().yMax
                            stateList.removeAt(stateList.lastIndex)
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

        mainPanel.addSelectedListener{rect->
            fp.plane?.let{
                val someState = State(Mandelbrot.funcNum, it.xMin, it.xMax, it.yMin, it.yMax, colorScheme, Julia.c)
                stateList.add(someState)//добавление состояния в список состояний
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



    private fun createMenuBar() { // функция, реализующая меню

        val menubar = JMenuBar()
        val file = JMenu("Файл")
        val  aMenuItem = JMenuItem("Сохранить картинку")
        file.add(aMenuItem) // добавление новой ячейки в меню
        aMenuItem.addActionListener{ _: ActionEvent -> } // сохранение картинки
        val  bMenuItem = JMenuItem("Отменить действие")
        file.add(bMenuItem)
        bMenuItem.addActionListener{
            fp.plane?.let {
                if(stateList.size != 0){
                    fp.pointColor = SchemeChooser(stateList.last().colorScheme)
                    it.xMin = stateList.last().xMin
                    it.yMin = stateList.last().yMin
                    it.xMax = stateList.last().xMax
                    it.yMax = stateList.last().yMax
                    stateList.removeAt(stateList.lastIndex)
                    mainPanel.repaint()
                }
            }
        }
        menubar.add(file)

        val  kMenuItem = JMenuItem("Сохранить файл")
        file.add(kMenuItem)
        kMenuItem.addActionListener{ _: ActionEvent -> save() } // сохранение картинки


        val file_color= JMenu("Выбор цветовой схемы")
        val  cMenuItem = JMenuItem("1")
        file_color.add(cMenuItem)
        val  dMenuItem = JMenuItem("2")
        file_color.add(dMenuItem)
        val  eMenuItem = JMenuItem("3")
        file_color.add(eMenuItem)
        menubar.add(file_color)


        val file_ecs = JMenu("Экскурсия по фракталу")
        val  fMenuItem = JMenuItem("начать")
        file_ecs.add(fMenuItem)
        menubar.add(file_ecs)
        jMenuBar = menubar

        val coordx = JTextField("-0.74543") //-0.8 //0.285 //-0.0085
        coordx.add(fMenuItem)
        menubar.add(coordx)
        jMenuBar=menubar

        val coordy = JTextField("0.11301") //0.156 //0.01 //0.71
        coordy.add(fMenuItem)
        menubar.add(coordy)
        jMenuBar=menubar

        var re: Double
        var im: Double

        coordx.addActionListener(ActionListener {
            var re0 = coordx.toString()
            re = re0.toDouble()
        })
        coordy.addActionListener(ActionListener {
            var im0 = coordy.toString()
            im = im0.toDouble()

        })

        val button = JButton("Нарисовать")
        button.addActionListener(object : ActionListener {
            override fun actionPerformed(e: ActionEvent) {
                val newWindow = JFrame("Julia")
                newWindow.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
                newWindow.setLocationRelativeTo(null)
                newWindow.isVisible = true
                newWindow.minimumSize = Dimension(600, 550)
                val textx = coordx.text.toString()
                val texty = coordy.text.toString()
                var re: Double
                var im: Double
                re = textx.toDouble()
                im = texty.toDouble()
                Julia.c = Complex(re, im)
                ju = FractalPainter(Julia)
                juliaPanel = DrawingPanel(ju)

                juliaPanel.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "pressed")

                juliaPanel.addComponentListener(object : ComponentAdapter(){
                    override fun componentResized(e: ComponentEvent?) {
                        ju.plane?.width = juliaPanel.width
                        ju.plane?.height = juliaPanel.height
                        newWindow.repaint()
                    }
                })
                juliaPanel.addKeyListener(object : KeyAdapter(){
                    override fun keyReleased(e: KeyEvent?) {
                        if (e != null) {
                            if (e.isControlDown){

                                ju.plane?.let {
                                    if(stateList.size != 0){
                                        ju.pointColor = SchemeChooser(stateList.last().colorScheme)
                                        it.xMin = stateList.last().xMin
                                        it.yMin = stateList.last().yMin
                                        it.xMax = stateList.last().xMax
                                        it.yMax = stateList.last().yMax
                                        stateList.removeAt(stateList.lastIndex)
                                        juliaPanel.repaint()
                                    }
                                }

                            }
                        }
                    }
                })
                juliaPanel.addSelectedListener {rect ->
                    ju.plane?.let {

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
                        juliaPanel.repaint()
                    }
                }

                juliaPanel.background = Color.WHITE
                layout = GroupLayout(contentPane).apply {
                    setVerticalGroup(
                        createSequentialGroup()
                            .addGap(8)
                            .addComponent(juliaPanel)
                            .addGap(8)
                    )
                    setHorizontalGroup(
                        createSequentialGroup()
                            .addGap(8)
                            .addComponent(juliaPanel)
                            .addGap(8)
                    )
                }
                pack()

                ju.plane = Plane(-2.0, 1.0, -1.0, 1.0, juliaPanel.width, juliaPanel.height)
                ju.pointColor = SchemeChooser(colorScheme)    //выбор цветовой схемы - всего 3
                newWindow.add(juliaPanel)
            }
        })
        menubar.add(button)
        jMenuBar = menubar


    }
    // реализация функции сохранения файла
    private fun save() {
        //val fileName = "имя_файла.расширение"
        var file: SimpleDateFormat? = null

        val fc = JFileChooser()
        fc.setDialogTitle("Сохранить файл")


        val frame = null
        if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                file = SimpleDateFormat()
                val path = fc.selectedFile.path
                val name = fc.selectedFile.name
                val date = file!!.format(Date())
                val fullPath = "$path/$name$date"

                println("Файл сохранен по адресу $fullPath")
            } catch (e: IOException) { // обработка исключений
                e.printStackTrace()
            }
        } else {
            println("Сохранение отменено")
        }
    }
    fun addState(state: State){
        stateList.add(state)
    }
}

data class State(val fractal: Int, val xMin: Double, val xMax: Double, val yMin: Double, val yMax: Double, val colorScheme: Int, val pointJulia: Complex?) {
}
