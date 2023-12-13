package ru.gr106.fractal.gui

import drawing.Plane
import java.awt.Component
import java.awt.Dimension
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException
import javax.swing.*
import javax.swing.GroupLayout.PREFERRED_SIZE

class JLabelSelection(val data: JLabel) : Transferable {
    override fun getTransferDataFlavors(): Array<DataFlavor> {
        return emptyArray()
    }

    override fun isDataFlavorSupported(flavor: DataFlavor?): Boolean {
        return true
    }

    override fun getTransferData(flavor: DataFlavor?): Any {
        return data
    }
}

class ListModelPlane(val keyFrames: MutableList<Plane>, val setT: (Double) -> Unit, val addP: () -> Unit) : JPanel() {
    var list: JList<JLabel>

    var model: DefaultListModel<JLabel> = DefaultListModel<JLabel>()
    var movedPlane = Plane(0.0, 0.0, 0.0, 0.0, 1, 1)
    val controlPanel: JPanel

    init {
        list = JList(model)
        list.dragEnabled = true
        list.dropMode = DropMode.INSERT
        //https://stackoverflow.com/questions/22266506/how-to-add-image-in-jlist
        list.cellRenderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
                label.icon = model[index].icon
                label.text = model[index].text
                label.verticalTextPosition = JLabel.CENTER
                return label
            }
        }
        // https://stackoverflow.com/questions/16586562/reordering-jlist-with-drag-and-drop
        // https://docs.oracle.com/javase/tutorial/uiswing/dnd/dropmodedemo.html
        list.transferHandler = object : TransferHandler() {
            var index = 0
            var beforeIndex =
                false //Start with `false` therefore if it is removed from or added to the list it still works

            @Override
            override fun getSourceActions(comp: JComponent): Int {
                return MOVE
            }

            @Override
            override fun createTransferable(comp: JComponent): Transferable {
                index = list.selectedIndex
                movedPlane = keyFrames[index]
                return JLabelSelection(list.getSelectedValue())
            }

            @Override
            override fun exportDone(comp: JComponent, trans: Transferable, action: Int) {
                if (action == MOVE) {
                    if (beforeIndex) {
                        model.remove(index + 1)
                        keyFrames.removeAt(index + 1)
                    } else {
                        model.remove(index)
                        keyFrames.removeAt(index)
                    }
                }
            }

            @Override
            override fun canImport(support: TransferSupport): Boolean {
                return true
            }

            @Override
            override fun importData(support: TransferSupport): Boolean {
                try {
                    val s = (support.getTransferable().getTransferData(DataFlavor.stringFlavor)) as JLabel
                    val dl: JList.DropLocation = (support.getDropLocation()) as JList.DropLocation
                    model.add(dl.index, s)
                    keyFrames.add(dl.index, movedPlane)
                    beforeIndex = (dl.index < index)
                    return true
                } catch (e: UnsupportedFlavorException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return false
            }
        }
        // http://www.java2s.com/Code/Java/Swing-JFC/AnexampleofJListwithaDefaultListModel.htm
        val pane = JScrollPane(list)
        val addButton = JButton("Добавить кадр")
        val removeButton = JButton("Удалить кадр")
        val burnButton = JButton("Создать")
        val timeLabel = JLabel("Длительность(сек.):")

        addButton.addActionListener {
            addP()
        }
        removeButton.addActionListener {
            removeSelectedItem()
        }
        burnButton.addActionListener {
            MovieMaker.makeVideo()
        }
        val timeChoose = SpinnerNumberModel(5.0, 1.0, 100.0, 0.1)
        timeChoose.addChangeListener {
            setT(timeChoose.value as Double)
        }
        val timeSpin = JSpinner(timeChoose)
        controlPanel = JPanel()
        controlPanel.layout = GroupLayout(controlPanel).apply {
            setVerticalGroup(
                createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(burnButton)
                    .addComponent(timeLabel)
                    .addComponent(timeSpin)
                    .addComponent(addButton)
                    .addComponent(removeButton)
            )
            setHorizontalGroup(
                createSequentialGroup()
                    .addComponent(burnButton)
                    .addComponent(timeLabel)
                    .addComponent(timeSpin)
                    .addComponent(addButton)
                    .addComponent(removeButton)
            )
        }
        layout = GroupLayout(this).apply {
            setVerticalGroup(
                createSequentialGroup()
                    .addComponent(pane)
                    .addComponent(controlPanel, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
            )
            setHorizontalGroup(
                createParallelGroup()
                    .addComponent(pane)
                    .addComponent(controlPanel, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
            )
        }
    }

    fun addItem(label: JLabel) {
        model.addElement(label)
    }

    fun removeSelectedItem() {
        if (model.size() > 0) {
            val i = list.anchorSelectionIndex
            model.removeElementAt(i)
            keyFrames.removeAt(i)
        }
    }

}

class FractalTourMenu : JFrame() {
    init {
        title = "Key frames list"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        contentPane = MovieMaker.cpJList
        setSize(260, 200)
        isVisible = true
        val cp = MovieMaker.cpJList.controlPanel
        minimumSize = Dimension(cp.width, 200)
        //maximumSize = Dimension(list.preferredSize.width, cp.width)
        maximumSize = Dimension(cp.width, Int.MAX_VALUE)
        //pack()
    }
}