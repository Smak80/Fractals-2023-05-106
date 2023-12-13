package ru.gr106.fractal.gui

import kotlin.math.max
import kotlin.math.min

class SelectionRect {
    private var p1: Pair<Int, Int>? = null
    private var p2: Pair<Int, Int>? = null
    val isCreated: Boolean
        get() = p2 != null
    val x: Int
        get() {
            p1?.let { pt1 ->
                p2?.let { pt2 ->
                    return min(pt1.first, pt2.first)
                }
            }
            return 0
        }
    val y: Int
        get() {
            p1?.let { pt1 ->
                p2?.let { pt2 ->
                    return min(pt1.second, pt2.second)
                }
            }
            return 0
        }
    val width: Int
        get() {
            p1?.let { pt1 ->
                p2?.let { pt2 ->
                    return max(pt1.first, pt2.first) - x
                }
            }
            return 0
        }
    val height: Int
        get() {
            p1?.let { pt1 ->
                p2?.let { pt2 ->
                    return max(pt1.second, pt2.second) - y
                }
            }
            return 0
        }

    fun addPoint(x: Int, y: Int) {
        p1?.let {
            p2 = x to y
        } ?: run {
            p1 = x to y
        }
    }

    fun notZero(): Boolean {
        val a = p1
        val b = p2
        return (a != null && b != null) && (a.first != b.first && a.second != b.second)
    }
}