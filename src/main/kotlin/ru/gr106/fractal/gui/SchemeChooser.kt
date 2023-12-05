package ru.gr106.fractal.gui

import math.AlgebraicFractal
import math.Complex
import java.awt.Color
import kotlin.math.*

fun SchemeChooser(SchemeNumber: Int,
                  Scheme1: (Float) -> Color = { if (it == 1f) Color(0,0,0) else
                      Color(0.0005f*(1- cos(255*it*it)).absoluteValue,
                          sin(105*it).absoluteValue,
                          cos(180*it).absoluteValue)
                  },
                  Scheme2: (Float) -> Color = { if (it == 1f) Color(0,0,0) else
                      Color(0.5f + 0.08f * cos((30*it * it)).absoluteValue,
                          sin(144 * it).absoluteValue,
                          log10((255 + it)/100).absoluteValue)
                  },
                  Scheme3: (Float) -> Color = { if (it == 1f) Color(0,0,0) else
                      Color(0.09f* log2(205f * it * it).absoluteValue,
                          0.13f* log2(105f * it).absoluteValue,
                          0.8f* log10(0.123f * (30f + it)).absoluteValue)
                  }): (Float) -> Color {
    if (SchemeNumber==1) return Scheme1
    else if (SchemeNumber==2) return Scheme2
    else return Scheme3
}
