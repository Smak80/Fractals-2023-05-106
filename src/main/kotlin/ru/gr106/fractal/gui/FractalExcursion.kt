package ru.gr106.fractal.gui

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import org.jcodec.api.awt.AWTSequenceEncoder
import org.jcodec.common.model.Size
import ru.smak.drawing.Plane
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.text.AttributeSet
import javax.swing.text.DocumentFilter
import javax.swing.text.PlainDocument

class FractalExcursion(val fp: FractalPainter) : JFrame("Экскурсия по фракталу") {
    private val frameListModel = DefaultListModel<DataPoint>()
    private val frameList = JList(frameListModel)
    private val addFrameButton = JButton("Добавить кадр")
    private val deleteFrameButton = JButton("Удалить кадр")
    private val makeVideoButton = JButton("Сделать видео")
    private val videoLengthInput = JTextField("5", 10)


    init {
        (videoLengthInput.document as PlainDocument).documentFilter = PositiveNumberFilter()
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        setSize(700, 200)
        minimumSize = Dimension(700, 200)
        isVisible = true

        layout = BorderLayout()

        frameList.cellRenderer = FrameListCellRenderer()
        add(JScrollPane(frameList), BorderLayout.CENTER)

        // кнопки
        val inputPanel = JPanel()
        inputPanel.add(JLabel("Длина видео:"))
        inputPanel.add(videoLengthInput)
        inputPanel.add(addFrameButton)
        inputPanel.add(deleteFrameButton)
        inputPanel.add(makeVideoButton)

        add(inputPanel, BorderLayout.NORTH)

        addFrameButton.addActionListener(ButtonClickListener())
        deleteFrameButton.addActionListener(ButtonClickListener())
        makeVideoButton.addActionListener(ButtonClickListener())
    }

    inner class ButtonClickListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            when (e.source) {
                addFrameButton -> {
                    // добавление кадра
                    val data = fp.plane?.let { DataPoint(1, it.xMin, it.xMax, it.yMin, it.yMax) }
                    frameListModel.addElement(data)
                }
                deleteFrameButton -> {
                    // удаление кадра
                    val selectedIndex = frameList.selectedIndex
                    if (selectedIndex != -1) {
                        frameListModel.remove(selectedIndex)
                    }
                }
                makeVideoButton -> {
                    // создание видео
                    createVideo()
                    println("Video created successfully.")
                }
            }
        }
    }

    inner class FrameListCellRenderer : ListCellRenderer<DataPoint> {
        private val renderer = JPanel(BorderLayout())
        private val numberLabel = JLabel()

        init {
            val font = numberLabel.font
            numberLabel.font = font.deriveFont(font.size * 3.5f) // Increase font size
            numberLabel.horizontalAlignment = SwingConstants.LEFT // Align text to the left
            renderer.add(numberLabel, BorderLayout.EAST) // Place the numberLabel to the right
            renderer.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        }
        override fun getListCellRendererComponent(
            list: JList<out DataPoint>?,
            value: DataPoint?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            numberLabel.text = "Кадр ${index + 1}"
            if (isSelected) {
                renderer.background = list?.selectionBackground
                renderer.foreground = list?.selectionForeground
            } else {
                renderer.background = list?.background
                renderer.foreground = list?.foreground
            }
            renderer.isEnabled = list?.isEnabled == true
            renderer.isOpaque = true
            return renderer
        }
    }

    data class DataPoint(val frameNumber: Int, val x: Double, val y: Double, val z: Double, val t: Double) {}

    private fun createVideo() {
        val fps = 30
        val time = videoLengthInput.text.toInt()
        println(time)
        val framesNumber = fps * time
        println(framesNumber)
        val numberOfPoints = frameListModel.size()
        println(numberOfPoints)
        //println(numberOfPoints)
        val temp = framesNumber / numberOfPoints

        val data: MutableList<DataPoint> = mutableListOf()
        for (i in 0 until  numberOfPoints) {
            data.add(DataPoint((temp * i).toInt(), frameListModel[i].x, frameListModel[i].y, frameListModel[i].z, frameListModel[i].t))
            println(DataPoint((temp * i).toInt(), frameListModel[i].x, frameListModel[i].y, frameListModel[i].z, frameListModel[i].t))
            //if (i != numberOfPoints-1) data.add(DataPoint(((temp * i) + temp/2).toInt(), 2.0, -1.0, 1.0, -1.0))
            //println(DataPoint((temp * i).toInt(), frameListModel[i].x, frameListModel[i].y, frameListModel[i].z, frameListModel[i].t))
        }

        // интерполяторы для каждой координаты
        val xInterpolator = SplineInterpolator().interpolate(
            data.map { it.frameNumber.toDouble() }.toDoubleArray(),
            data.map { it.x }.toDoubleArray()
        )
        val yInterpolator = SplineInterpolator().interpolate(
            data.map { it.frameNumber.toDouble() }.toDoubleArray(),
            data.map { it.y }.toDoubleArray()
        )
        val zInterpolator = SplineInterpolator().interpolate(
            data.map { it.frameNumber.toDouble() }.toDoubleArray(),
            data.map { it.z }.toDoubleArray()
        )
        val tInterpolator = SplineInterpolator().interpolate(
            data.map { it.frameNumber.toDouble() }.toDoubleArray(),
            data.map { it.t }.toDoubleArray()
        )

        val start = data.first().frameNumber
        val end = data.last().frameNumber

        var InterpolatedData: MutableList<DataPoint> = mutableListOf()

        // запись интерполированных значений
        for (j in start until end) {
            val currentFrameNumber = j
            val interpolatedX = xInterpolator.value(currentFrameNumber.toDouble())
            val interpolatedY = yInterpolator.value(currentFrameNumber.toDouble())
            val interpolatedZ = zInterpolator.value(currentFrameNumber.toDouble())
            val interpolatedT = tInterpolator.value(currentFrameNumber.toDouble())
            InterpolatedData.add(DataPoint(currentFrameNumber.toInt(), interpolatedX, interpolatedY, interpolatedZ, interpolatedT))
        }

        val outputFile = File("output_video.mp4")
        val encoder = AWTSequenceEncoder.createSequenceEncoder(outputFile, fps)
        for (i in start until end) {
            print("Making fractal excursion - ")
            print(i)
            print("/")
            println(end)
            fp.plane = Plane(InterpolatedData[i].x, InterpolatedData[i].y, InterpolatedData[i].z, InterpolatedData[i].t, 1920, 1080)
            val image = panelToImage(fp)
            encoder.encodeImage(image)
            //println(InterpolatedData[i].toString())
        }
        encoder.finish()
    }

    // перевод фрактала в изображение
    fun panelToImage(plane: FractalPainter): BufferedImage {
        val imageSize = Size(1920, 1080)
        val image = BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()
        DrawingPanel(plane).paint(g)
        g.dispose()
        return image
    }

    // фильтр для ввода времени
    inner class PositiveNumberFilter : DocumentFilter() {
        override fun insertString(fb: FilterBypass?, offset: Int, text: String?, attr: AttributeSet?) {
            if (text?.matches(Regex("[0-9]*")) == true) {
                super.insertString(fb, offset, text, attr)
            }
        }
        override fun replace(fb: FilterBypass?, offset: Int, length: Int, text: String?, attrs: AttributeSet?) {
            if (text?.matches(Regex("[0-9]*")) == true) {
                super.replace(fb, offset, length, text, attrs)
            }
        }
    }
}