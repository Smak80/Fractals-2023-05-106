package ru.gr106.fractal.gui

import org.jcodec.api.awt.AWTSequenceEncoder
import java.awt.Image
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File

class FractalExcursion(private val framesPerSecond: Int = 30) {

    fun createVideo(fragments: Array<Rectangle>, outputFile: File) {
        val robot = Robot()
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val screenRect = Rectangle(screenSize)

        // Создаем объект кодировщика видео
        val encoder = AWTSequenceEncoder.createSequenceEncoder(outputFile, framesPerSecond)

        for (fragment in fragments) {
            for (i in 0 until framesPerSecond * 3) { // Пример: плавное перемещение в течение 3 секунд
                val progress = i.toDouble() / (framesPerSecond * 3 - 1)
                val x = (fragment.x + progress * 100).toInt() // Пример: перемещение по оси X
                val y = (fragment.y + progress * 100).toInt() // Пример: перемещение по оси Y

                val screenCapture = robot.createScreenCapture(Rectangle(screenRect.x + x, screenRect.y + y, fragment.width, fragment.height))
                val bufferedImage = BufferedImage(screenCapture.width, screenCapture.height, BufferedImage.TYPE_INT_RGB)
                bufferedImage.graphics.drawImage(screenCapture, 0, 0, null)

                encoder.encodeImage(bufferedImage)
            }
        }

        // Завершаем процесс кодирования и закрываем кодировщик
        encoder.finish()
    }
}


