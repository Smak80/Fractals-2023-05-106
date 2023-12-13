package ru.gr106.fractal

import math.Mandelbrot
import ru.gr106.fractal.gui.Window

fun main() {
    Window(Mandelbrot).apply { isVisible = true }
}