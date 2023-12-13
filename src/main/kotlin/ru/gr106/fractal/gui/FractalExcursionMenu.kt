package ru.gr106.fractal.gui

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

class FractalExcursionMenu : JFrame("Видео Редактор") {
    private val frameListModel = DefaultListModel<String>()
    private val frameList = JList(frameListModel)
    private val addFrameButton = JButton("Добавить кадр")
    private val deleteFrameButton = JButton("Удалить кадр")
    private val makeVideoButton = JButton("Сделать видео")
    private val lengthInput = JTextField(10)
    private val videoLengthInput = JTextField(10)

    init {
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        setSize(700, 200)
        minimumSize = Dimension(700, 200)
        isVisible = true

        // Используем BorderLayout для размещения компонентов
        layout = BorderLayout()

        // Панель для размещения списка кадров
        frameListModel.addElement("Кадр 1")
        frameList.cellRenderer = FrameListCellRenderer()
        add(JScrollPane(frameList), BorderLayout.CENTER)

        // Панель для размещения текстовых полей и кнопок
        val inputPanel = JPanel()
        inputPanel.add(JLabel("Длина видео:"))
        inputPanel.add(videoLengthInput)
        inputPanel.add(addFrameButton)
        inputPanel.add(deleteFrameButton)
        inputPanel.add(makeVideoButton)

        // Добавляем компоненты на форму
        add(inputPanel, BorderLayout.NORTH)

        addFrameButton.addActionListener(ButtonClickListener())
        deleteFrameButton.addActionListener(ButtonClickListener())
        makeVideoButton.addActionListener(ButtonClickListener())
    }

    inner class ButtonClickListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            when (e.source) {
                addFrameButton -> {
                    // Добавление кадра в список
                    val frameNumber = frameListModel.size + 1
                    frameListModel.addElement("Кадр $frameNumber")
                }
                deleteFrameButton -> {
                    // Удаление выбранного кадра из списка
                    val selectedIndex = frameList.selectedIndex
                    if (selectedIndex != -1) {
                        frameListModel.remove(selectedIndex)
                    }
                }
                makeVideoButton -> {
                    // Обработка кнопки "Сделать видео"
                    val length = lengthInput.text
                    val videoLength = videoLengthInput.text
                    println("Сделать видео с длиной $length секунд и длиной видео $videoLength секунд")
                }
            }
        }
    }

    inner class FrameListCellRenderer : ListCellRenderer<String> {
        private val renderer = JPanel(BorderLayout())
        private val textLabel = JLabel()

        init {
            renderer.add(textLabel, BorderLayout.CENTER)
            renderer.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        }

        override fun getListCellRendererComponent(
            list: JList<out String>?,
            value: String?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            textLabel.text = value
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
}

