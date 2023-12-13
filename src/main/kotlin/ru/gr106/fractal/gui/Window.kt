package ru.gr106.fractal.gui

import drawing.Converter
import drawing.Plane
import math.AlgebraicFractal
import math.Complex
import math.Julia
import math.Mandelbrot
import ru.gr106.fractal.main
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import java.time.YearMonth
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.GroupLayout.PREFERRED_SIZE
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.math.*

class Window(f: AlgebraicFractal) : JFrame() {
    private val af = f
    private val mainPanel: DrawingPanel
    private val fp: FractalPainter
    var themes: Map<String, (Float) -> Color> = mapOf()
    var funcs: Map<String, (Complex) -> Complex> = mapOf()
    private var cancelAction: Stack<Map<Pair<Double, Double>, Pair<Double, Double>>>
    private var newWidth: Int = 0
    private var newHeight: Int = 0
    private var dx: Double = 0.0
    private var dy: Double = 0.0
    private var yMin = -1.0
    private var yMax = 1.0
    private var xMin = -2.0
    private var xMax = 1.0


    init {
        fp = FractalPainter(af)
        val menuBar = createMenuBar()
        if(af is Mandelbrot)
            defaultCloseOperation = EXIT_ON_CLOSE
        else
            defaultCloseOperation = DISPOSE_ON_CLOSE
        minimumSize = Dimension(600, 550)
        mainPanel = DrawingPanel(fp)
        cancelAction = Stack<Map<Pair<Double, Double>, Pair<Double, Double>>>()

        funcs = mapOf(
            "square" to {value:Complex -> value*value},
            "qubic" to {value:Complex -> value*value*value},
            "plus" to {value:Complex -> value+value}
        )

        themes = mapOf(
            "green" to {
                if (it == 1f) Color.BLACK else
                    Color(
                        0.5f * (1 - cos(16f * it * it)).absoluteValue,
                        sin(5f * it).absoluteValue,
                        log10(1f + 5 * it).absoluteValue
                    )
            },
            "red" to {
                if (it == 1f) Color.BLACK else
                    Color(
                        cos(it + PI * (0.5 + sin(it))).absoluteValue.toFloat(),
                        cos(it + PI * (0.5 + cos(it))).absoluteValue.toFloat(),
                        (0.1 * cos(it)).absoluteValue.toFloat(),
                    )

            },
            "red-blue" to {
                if (it == 1f) Color.BLACK else
                    Color(
                        (0.5*cos(it + PI * (0.5 + it))).absoluteValue.toFloat(),
                        (0.1*cos(it + PI * (0.5 + sin(it)))).absoluteValue.toFloat(),
                        (2 * atan(it*tan(it) + PI * (tan(it)*tan(it))) / PI).absoluteValue.toFloat(),
                    ).brighter()
            },
            "yellow-green" to {
                if (it == 1f) Color.BLACK else
                    Color(
                        (2 * asin(it + PI * (sin(it))) / PI).absoluteValue.toFloat(),
                        (2 * atan(it + PI * (tan(it))) / PI).absoluteValue.toFloat(),
                        (2 * acos(it + PI * (cos(it))) / PI).absoluteValue.toFloat()
                    )
            },
            "lilac" to {
                if (it == 1f) Color.BLACK else
                    Color(
                        cos(it + PI*(0.5 + it)).absoluteValue.toFloat(),
                        (2*atan(it + PI*(tan(it)))/ PI).absoluteValue.toFloat(),
                        cos(it+PI*(0.5+sin(it))).absoluteValue.toFloat(),
                    )
            }
        )

        mainPanel.addComponentListener(object : ComponentAdapter() {
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

                //if (Math.abs(relationOXY - relationWidthHeight) > 1E-5){
                    if (relationOXY < relationWidthHeight) {
                        dx = (OXlength / relationOXY * relationWidthHeight - OXlength) / 2
                        newXMin -= dx
                        newXMax += dx
                    }
                    if (relationOXY > relationWidthHeight) {
                        dy = (OYlength / relationWidthHeight * relationOXY - OYlength) / 2
                        newYMin -= dy
                        newYMax += dy
                    }
                //}
                val xPair = Pair(newXMin, newXMax)
                val yPair = Pair(newYMin, newYMax)
                val mapOfCoord = mutableMapOf<Pair<Double, Double>, Pair<Double, Double>>()
                mapOfCoord.put(xPair, yPair)
                cancelAction.push(mapOfCoord)
                fp.plane?.xMax = newXMax
                fp.plane?.xMin = newXMin
                fp.plane?.yMin = newYMin
                fp.plane?.yMax = newYMax
                fp.plane?.height = mainPanel.height
                fp.plane?.width = mainPanel.width


                fp.previous_img = null
                mainPanel.repaint()
            }
        })
        mainPanel.addSelectedListener { rect ->
            fp.plane?.let {
                val _xMin = Converter.xScr2Crt(rect.x, it)
                val _xMax = Converter.xScr2Crt(rect.x + rect.width, it)
                val _yMin = Converter.yScr2Crt(rect.y + rect.height, it)
                val _yMax = Converter.yScr2Crt(rect.y, it)
                newHeight = mainPanel.height
                newWidth = mainPanel.width
                xMin = _xMin
                xMax = _xMax
                yMin = _yMin
                yMax = _yMax

                val OXlength = _xMax - _xMin
                val OYlength = _yMax - _yMin

                var newXMax = xMax
                var newXMin = xMin
                var newYMax = yMax
                var newYMin = yMin

                val relationOXY = OXlength * 1.0 / OYlength

                val relationWidthHeight = newWidth * 1.0 / newHeight

                if (relationOXY < relationWidthHeight) {
                    dx = (OXlength / relationOXY * relationWidthHeight - OXlength) / 2
                    newXMin -= dx
                    newXMax += dx
                }
                if (relationOXY > relationWidthHeight) {
                    dy = (OYlength / relationWidthHeight * relationOXY - OYlength) / 2
                    newYMin -= dy
                    newYMax += dy
                }

                xMax = newXMax
                xMin = newXMin
                yMax = newYMax
                yMin = newYMin
                it.xMin = newXMin
                it.yMin = newYMin
                it.xMax = newXMax
                it.yMax = newYMax

                val mapOfCoord = mutableMapOf<Pair<Double, Double>, Pair<Double, Double>>()
                val pairX = Pair(newXMin, newXMax)
                val pairY = Pair(newYMin, newYMax)
                mapOfCoord.put(pairX, pairY)
                cancelAction.push(mapOfCoord)
                fp.previous_img = null
                mainPanel.repaint()
            }
        }
        mainPanel.background = Color.WHITE

        if (af is Mandelbrot)
            layout = GroupLayout(contentPane).apply {
                setVerticalGroup(
                    createSequentialGroup()
                        .addComponent(menuBar, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
                        .addGap(4)
                        .addComponent(mainPanel)
                        .addGap(8)

                )
                setHorizontalGroup(
                    createParallelGroup()
                        .addComponent(menuBar)
                        .addGroup(
                            createSequentialGroup()
                                .addGap(8)
                                .addComponent(mainPanel)
                                .addGap(8)
                        )
                        .addGap(4)
                )
            }
        else {
            val lblXCord = JLabel("X: ")
            val lblYCord = JLabel("Y: ")
            val mdlXCord = SpinnerNumberModel((af as Julia).x, null, null, 0.00001)
            val mdlYCord = SpinnerNumberModel((af as Julia).y, null, null, 0.00001)
            val spnXCord = JSpinner(mdlXCord)
            val spnYCord = JSpinner(mdlYCord)

            mdlXCord.addChangeListener{
                (af as Julia).x = mdlXCord.value as Double
                fp.previous_img = null
                mainPanel.repaint()
            }
            mdlYCord.addChangeListener{
                (af as Julia).y = mdlYCord.value as Double
                fp.previous_img = null
                mainPanel.repaint()
            }

            spnXCord.setEditor(JSpinner.NumberEditor(spnXCord, "0.00000"));
            spnYCord.setEditor(JSpinner.NumberEditor(spnYCord, "0.00000"));

            layout = GroupLayout(contentPane).apply {
                setVerticalGroup(
                    createSequentialGroup()
                        .addComponent(menuBar, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
                        .addGap(4)
                        .addComponent(mainPanel)
                        .addGap(4)
                        .addGroup(
                            createParallelGroup()
                                .addComponent(lblXCord)
                                .addComponent(spnXCord, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
                                .addComponent(lblYCord)
                                .addComponent(spnYCord, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
                        )
                        .addGap(8)


                )
                setHorizontalGroup(
                    createParallelGroup()
                        .addComponent(menuBar)
                        .addGroup(
                            createSequentialGroup()
                                .addGap(8)
                                .addComponent(mainPanel)
                                .addGap(8)
                        )
                        .addGroup(
                            createSequentialGroup()
                                .addGap(8)
                                .addComponent(lblXCord)
                                .addGap(4)
                                .addComponent(spnXCord)
                                .addGap(4)
                                .addComponent(lblYCord)
                                .addGap(4)
                                .addComponent(spnYCord)
                                .addGap(8)
                        )
                        .addGap(4)
                )
            }

        }
        pack()
        fp.plane = Plane(-2.0, 1.0, -1.0, 1.0, mainPanel.width, mainPanel.height)
        fp.pointColor = themes["green"]!!
        MovieMaker.fpp = fp
    }
    private fun createMenuBar(): JMenuBar {
        val menuBar = JMenuBar()
        this.add(menuBar)
        val file = JMenu("Файл")
        file.setMnemonic('Ф')
        menuBar.add(file)

        val saveJPG = JMenuItem("Сохранить картинку")
        file.add(saveJPG)
        saveJPG.addActionListener { _: ActionEvent -> saveJPGFunc() }

        val save = JMenuItem("Сохранить проект")
        file.add(save)
        save.addActionListener { _: ActionEvent -> saveFunc() }

        val load = JMenuItem("Загрузить проект")
        file.add(load)
        load.addActionListener { _: ActionEvent -> loadFunc() }

        val edit = JMenu("Изменить")
        edit.setMnemonic('И')
        menuBar.add(edit)

        val undo = JMenuItem("Назад")
        edit.add(undo)
        undo.addActionListener { _: ActionEvent -> undoFunc() }
        undo.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, toolkit.menuShortcutKeyMaskEx)


        val redo = JMenuItem("Вперёд")
        edit.add(redo)
        redo.addActionListener { _: ActionEvent -> redoFunc() }

        val theme = JMenu("Тема")
        edit.add(theme)
        theme.setMnemonic('Т')

        val DynamicIteration = JMenu("Динамические итерации")
        menuBar.add(DynamicIteration)

        val turnOn = JMenuItem("Включить")
        DynamicIteration.add(turnOn)
        turnOn.addActionListener{_: ActionEvent ->
            DYTurnOn()
            fp.previous_img = null
            mainPanel.repaint()
        }

        val turnOff = JMenuItem("Выключить")
        DynamicIteration.add(turnOff)
        turnOff.addActionListener{_: ActionEvent ->
            DYTurnOff()
            fp.previous_img = null
            mainPanel.repaint()
        }
        val FractFunc = JMenu("Функция построения")
        menuBar.add(FractFunc)

        val squareFunc = JMenuItem("Квадратичная")
        FractFunc.add(squareFunc)
        squareFunc.addActionListener{_: ActionEvent ->
            Mandelbrot.function = funcs["square"]!!
            fp.previous_img = null
            mainPanel.repaint()
        }

        val qubicFunc = JMenuItem("Кубическая")
        FractFunc.add(qubicFunc)
        qubicFunc.addActionListener{_: ActionEvent ->
            Mandelbrot.function = funcs["qubic"]!!
            fp.previous_img = null
            mainPanel.repaint()
        }

        val expFunc = JMenuItem("Линейная")
        FractFunc.add(expFunc)
        expFunc.addActionListener{_: ActionEvent ->
            Mandelbrot.function = funcs["plus"]!!
            fp.previous_img = null
            mainPanel.repaint()
        }


        val greenTheme = JMenuItem("Зелёная тема")
        theme.add(greenTheme)
        greenTheme.setMnemonic('З')
        greenTheme.addActionListener { _: ActionEvent ->
            fp.pointColor = themes["green"]!!
            fp.previous_img = null
            mainPanel.repaint()
        }

        val redTheme = JMenuItem("Красная тема")
        theme.add(redTheme)
        redTheme.setMnemonic('К')
        redTheme.addActionListener { _: ActionEvent ->
            fp.pointColor = themes["red"]!!
            fp.previous_img = null
            mainPanel.repaint()
        }

        val lilacTheme = JMenuItem("Красно-синяя тема")
        theme.add(lilacTheme)
        lilacTheme.setMnemonic('С')
        lilacTheme.addActionListener { _: ActionEvent ->
            fp.pointColor = themes["red-blue"]!!
            fp.previous_img = null
            mainPanel.repaint()
        }

        val yellowGreenTheme = JMenuItem("Желто-зелёная тема")
        theme.add(yellowGreenTheme)
        yellowGreenTheme.setMnemonic('Ж')
        yellowGreenTheme.addActionListener { _: ActionEvent ->
            fp.pointColor = themes["yellow-green"]!!
            fp.previous_img = null
            mainPanel.repaint()
        }

        val observe = JMenu("Обозреть")
        observe.setMnemonic('О')
        menuBar.add(observe)
        observe.addActionListener { _: ActionEvent -> joulbertFunc() }

        if(af !is Julia) {
            val joulbert = JMenuItem("Отрисовать множество Жюлиа")
            joulbert.setMnemonic('Ж')
            joulbert.addActionListener { _: ActionEvent -> joulbertFunc() }
            observe.add(joulbert)
        }

        val view = JMenuItem("Экскурсия")
        view.setMnemonic('Э')
        view.addActionListener { _: ActionEvent -> viewFunc() }
        observe.add(view)
        return menuBar
    }

    private fun loadFunc() {
        val fileChooser = JFileChooser()
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES)
        val ok = fileChooser.showOpenDialog(null)
        if (ok==0) {
            val path: String? = fileChooser.selectedFile.toString()
            val parts = Scanner(File(path)).nextLine().split(" ")
            fp.plane?.let { p ->
                p.xMin = parts[0].toDouble()
                p.xMax = parts[1].toDouble()
                p.yMin = parts[2].toDouble()
                p.yMax = parts[3].toDouble()
                fp.pointColor = themes[parts[4]]!!
                fp.previous_img= null
                mainPanel.repaint()
            }
        }
    }

    private fun joulbertFunc() {
        Window(Julia()).apply { isVisible = true
            title = "Множество Жюлиа"
        }
    }

    private fun redoFunc() {
        JOptionPane.showMessageDialog(null ,"Не удалось сохранить файл", "", JOptionPane.ERROR_MESSAGE)

    }
    private fun saveJPGFunc(){
        val fileChooser = JFileChooser("C:\\Desktop")
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter(".jpg", "jpg"))
        fileChooser.isAcceptAllFileFilterUsed = false
        try {
            val ok = fileChooser.showSaveDialog(this)
            if (ok == 0) {
                var path: String? = fileChooser.selectedFile.toString()
                //val pth = path?.replace("\\s".toRegex(), "")
                if (path.isNullOrEmpty() || path == "C:\\") path = null
                else if (!path.endsWith(".jpg")) path += ".jpg"

                var bufferedImage = BufferedImage(
                    fp.width + 10,
                    fp.height + 40,
                    BufferedImage.TYPE_INT_RGB
                )
                val g: Graphics = bufferedImage.createGraphics().also {
                    it.color = Color.WHITE
                }
                fp.previous_img?.let {
                    g.drawImage(
                        it,
                        10,
                        0,
                        null
                    )
                    //g.drawLine(0, 0, 0, bufferedImage.height)

                    fp.plane?.let { plane ->
                        val epsY = Converter.yScr2Crt(0, plane) - Converter.yScr2Crt(1, plane)
                        var step = (Converter.yScr2Crt(fp.height, plane) - Converter.yScr2Crt(0, plane)) / 8.0
                        for (yS in 0..fp.height) {
                            val y = Converter.yScr2Crt(yS, plane)
                            var h = 5
                            if (abs(y % step) < epsY) {
                                if (abs(y % (2 * step)) < epsY) {
                                    h += 5
                                }
                                g.drawLine(0, yS, h, yS)
                            }
                        }

                        val string1 = "XMin = ${Converter.xScr2Crt(0, plane)}," +
                                " XMax = ${Converter.xScr2Crt(fp.width, plane)}"
                        val string2 = "YMin = ${Converter.yScr2Crt(0, plane)}," +
                                " YMax = ${Converter.yScr2Crt(fp.height, plane)}"
                        with(g.fontMetrics.getStringBounds(string1, g)) {
                            g.drawString(
                                string1,
                                ((fp.width / 2) - width / 2).toInt(),
                                (bufferedImage.height - height).toInt()
                            )
                            g.drawString(
                                string2,
                                ((fp.width / 2) - width / 2).toInt(),
                                (bufferedImage.height).toInt()
                            )
                            val epsX = Converter.xScr2Crt(1, plane) - Converter.xScr2Crt(0, plane)
                            step = (Converter.xScr2Crt(fp.width, plane) - Converter.xScr2Crt(0, plane)) / 8.0
                            for (xS in 0..fp.width) {
                                val x = Converter.xScr2Crt(xS, plane)
                                var h = 5
                                if (abs(x % step) < epsX) {
                                    if (abs(x % (2 * step)) < epsX) {
                                        h += 5
                                    }
                                    g.drawLine(
                                        xS, (bufferedImage.height - 2 * height).toInt(),
                                        xS, (bufferedImage.height - 2 * height).toInt() - h
                                    )
                                }
                            }
                        }
                    }
                }

                path?.let {
                    ImageIO.write(bufferedImage, "jpg", File(it))
                }
            }
        } catch(e: Exception){
            JOptionPane.showMessageDialog(null ,
                "Не удалось сохранить файл",
                "",
                JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun saveFunc() {
        val fileChooser = JFileChooser()
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
        val ok = fileChooser.showSaveDialog(null)
        if (ok==0) {
            var path: String? = fileChooser.selectedFile.toString()
            if(path!!.length >=4){
                if(!path.toString().slice((path.length-4)..<(path.length)).equals(".txt")){
                    if(path[path.length-1] == '.') path += "txt"
                    else path += ".txt"
                }
                fp.plane?.let { p ->
                    val xMin = p.xMin.toString()+" "
                    val xMax = p.xMax.toString()+" "
                    val yMin = p.yMin.toString()+" "
                    val yMax = p.yMax.toString()+" "
                    File(path).writeText(xMin+xMax+yMin+yMax+themes.filter { fp.pointColor == it.value }.keys.first())
                }
            }
        }
    }

    private fun viewFunc() {
        FractalTourMenu()
    }

    private fun undoFunc() {
        if (cancelAction.size != 1) {
            cancelAction.pop()
            var afterRemoval = mutableMapOf<Pair<Double, Double>, Pair<Double, Double>>()
            afterRemoval = cancelAction.peek() as MutableMap<Pair<Double, Double>, Pair<Double, Double>>
            val xPair = afterRemoval.keys.toList()
            val yPair = afterRemoval.values.toList()
            val xMin = xPair[0].first
            val xMax = xPair[0].second
            val yMin = yPair[0].first
            val yMax = yPair[0].second
            fp.plane?.xMax = xMax
            fp.plane?.xMin = xMin
            fp.plane?.yMax = yMax
            fp.plane?.yMin = yMin
        }
        fp.previous_img = null
        mainPanel.repaint()
    }

    private fun DYTurnOn(){

    }
    private fun DYTurnOff(){

    }

}