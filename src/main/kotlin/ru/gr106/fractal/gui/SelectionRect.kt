package ru.gr106.fractal.gui

import kotlin.math.max
import kotlin.math.min

class SelectionRect {
    private var p1: Pair<Int,Int>? = null
    private var p2: Pair<Int,Int>? = null

    var difX: Int = 0
    var difY: Int = 0

    val isCreated: Boolean
        get() = p2 != null
    val x: Int
        get() {
            p1?.let {pt1 ->
                p2?.let {pt2 ->
                    return min(pt1.first,pt2.first)
                }
            }
            return 0
        }
    val y: Int
    get() {
        p1?.let {pt1 ->
            p2?.let {pt2 ->
                return min(pt1.second,pt2.second)
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

    fun addPoint(x:Int, y:Int){
        p1?.let {
            p2 = x to y
        } ?: run {
            p1 = x to y
        }
    }

    //данная перегрузка создавалась для двигания фрактала, но в итоге не понадобилась
    fun addPoint(x1: Int, y1: Int, x2: Int, y2: Int){
        p1 = x1 to y1
        p2 = x2 to y2
    }

    //обнуление координат нажатия мыши
    fun resetPoints(){
        p1 = 0 to 0
        p2 = 0 to 0
    }

    fun resetSecondPoint(){
        p2 = null
    }
}