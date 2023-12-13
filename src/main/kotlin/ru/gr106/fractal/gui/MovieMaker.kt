package ru.gr106.fractal.gui

import drawing.EncodeVideo
import drawing.Plane
import math.Exponent
import math.Mandelbrot
import math.splines.CubicMomentSpline
import math.splines.LinearSpline
import math.splines.Spline
import java.awt.image.BufferedImage
import java.util.concurrent.LinkedBlockingQueue
import javax.swing.ImageIcon
import javax.swing.JLabel
import kotlin.concurrent.thread
import kotlin.math.abs
import kotlin.math.ln
import kotlin.time.TimeSource


object MovieMaker {
    var fpp = FractalPainter(Mandelbrot)
    val controlPoints = mutableListOf<Plane>()

    /// time in seconds
    var T = 5.0
    val fps = 24
    val outputFileName = "output.mp4"

    fun setTime(t: Double) {
        T = t
    }

    // Content Pane JList
    val cpJList = ListModelPlane(controlPoints, ::setTime, ::addControlPoint)

    fun addControlPoint() {
        fpp.plane?.let { p ->
            controlPoints.add(p.copy())
            val pp = p.copy()
            p.width /= 4
            p.height /= 4
            val buff = BufferedImage(p.width, p.height, BufferedImage.TYPE_INT_RGB)
            fpp.previous_img = null
            fpp.paint(buff.graphics)
            fpp.previous_img = null
            cpJList.addItem(JLabel(pp.str(), ImageIcon(buff), JLabel.RIGHT))
            fpp.plane = pp
        }
    }

    fun printKeyFrames() {
        for (p in controlPoints) {
            println(p.toString())
        }
    }

    fun printList(list: List<Any>) {
        for (d in list) {
            print("$d ")
        }
        println()
    }

    class Approx(
        val t: ArrayList<Double>,
        val c1: Double,
        val t0: Double,
        val segmentT: MutableList<Double>,
        val E: MutableList<Exponent>,
        val S: MutableList<Spline>,
        val sizes: Array<Double>
    ) {
        var k = 0
        var size = 0.0
        var corner = 0.0

        fun find(t_0: Double) {
            while (segmentT[k + 1] < t_0) {
                k++
            }
            if (segmentType[k] == CONST) {
                size = sizes[frameInd[k]]
                corner = S[k].sb(t_0)
            } else {
                size = E[k].sb(t_0)
                corner = S[k].sb(size)
            }
        }

        fun reset() {
            k = 0
            size = 0.0
            corner = 0.0
        }
    }


    // Segment type
    val INCREASE = 1
    val DECREASE = -1
    val CONST = 0

    val EPS = 0.0000000001
    private fun type(x: Double): Int {
        if (abs(x) < EPS) {
            return CONST
        }
        if (x < 0.0) {
            return DECREASE
        }
        if (x > 0.0) {
            return INCREASE
        }
        return CONST
    }

    val segmentType = mutableListOf<Int>()
    val frameInd = mutableListOf<Int>()

    // coeffs, when segment type is CONST
    val coeff = mutableListOf<Double>()

    // ds
    val coefLists = mutableListOf<MutableList<Double>>()

    private fun ind(i: Int): Int {
        return frameInd[i]
    }

    private fun calcCoeff(
        xsizes: Array<Double>,
        ysizes: Array<Double>,
        xcorner: Array<Double>,
        ycorner: Array<Double>
    ) {
        val N = frameInd.size
        for (i in 1..<N) {
            val type = segmentType[i - 1]
            if (type == CONST) {
                var K = 0.0
                val list = mutableListOf<Double>()
                for (j in ind(i - 1) + 1..ind(i)) {
                    val da = abs(xcorner[j] - xcorner[j - 1])
                    val db = abs(ycorner[j] - ycorner[j - 1])
                    // here we think, that c1X = c1Y
                    val k1 = (db / ysizes[j] + da / xsizes[j]) / 2.0
                    list.add(k1)
                    K += k1
                }
                coeff.add(K)
                coefLists.add(list)
            } else {
                // fake coeffs for saving index order
                coeff.add(0.0)
                coefLists.add(mutableListOf())
            }
        }
    }

    // all sizes, all corners
    private fun setUpSystem(sizes: Array<Double>): ArrayList<Double> {
        val N = frameInd.size
        val a: Array<Double> = Array(N) { 0.0 }
        a[0] = T
        for (i in 1..<N) {
            val type = segmentType[i - 1]
            if (type == CONST) {
                a[i] = coeff[i - 1]
            } else {
                val s2 = sizes[ind(i)]
                val s1 = sizes[ind(i - 1)]
                a[i] = ln(s2) - ln(s1)
                if (type == DECREASE) {
                    a[i] = -a[i]
                }
            }
        }
        return solveSystem(a)
    }

    // all sizes, all corners
    private fun findApprox(sizes: Array<Double>, corners: Array<Double>): Approx {
        val t = setUpSystem(sizes)
        val c1 = t[0]
        val t0 = when (segmentType[0]) {
            CONST -> 0.0
            INCREASE -> ln(sizes[0]) / c1
            DECREASE -> -ln(sizes[0]) / c1
            else -> {
                print("WTF")
                0.0
            }
        }
        t.removeAt(0)
        val segmentT = mutableListOf<Double>()
        segmentT.add(0.0)
        var sum = 0.0
        for (dt in t) {
            sum += dt
            segmentT.add(sum)
        }
        /// Awful bag fix
        // sometimes last time point < then T
        // this fix that
        segmentT[segmentT.size - 1] = T

        val E = mutableListOf<Exponent>()
        val S = mutableListOf<Spline>()
        for (i in 0..<frameInd.size - 1) {
            when (segmentType[i]) {
                CONST -> E.add(Exponent.FAKEE)
                INCREASE -> E.add(Exponent(c1, t0, segmentT[i], sizes[ind(i)]))
                DECREASE -> E.add(Exponent(-c1, t0, segmentT[i], sizes[ind(i)]))
            }
            val i1 = frameInd[i]
            val i2 = frameInd[i + 1]

            when (segmentType[i]) {
                CONST -> {
                    val cn = corners.copyOfRange(i1, i2 + 1)
                    val tt = mutableListOf<Double>()
                    var t1 = segmentT[i]
                    tt.add(t1)
                    for (ds in coefLists[i]) {
                        val dt = ds / c1
                        t1 += dt
                        tt.add(t1)
                    }
                    // wtf
                    tt[tt.size - 1] = segmentT[i + 1]
                    // number of segments
                    val N = tt.size - 1
                    if (N == 1) {
                        S.add(LinearSpline(N, tt.toDoubleArray(), cn.toDoubleArray()))
                    } else {
                        S.add(CubicMomentSpline(N, tt.toDoubleArray(), cn.toDoubleArray()))
                    }
                }

                INCREASE -> {
                    val sz = sizes.copyOfRange(i1, i2 + 1)
                    val cn = corners.copyOfRange(i1, i2 + 1)
                    val N = sz.size - 1
                    S.add(CubicMomentSpline(N, sz.toDoubleArray(), cn.toDoubleArray()))
                }

                DECREASE -> {
                    val sz = sizes.copyOfRange(i1, i2 + 1)
                    val cn = corners.copyOfRange(i1, i2 + 1)
                    sz.reverse()
                    cn.reverse()
                    val N = sz.size - 1
                    S.add(CubicMomentSpline(N, sz.toDoubleArray(), cn.toDoubleArray()))
                }
            }
        }
        val app = Approx(t, c1, t0, segmentT, E, S, sizes)
        return app
    }

    private fun solveSystem(a: Array<Double>): ArrayList<Double> {
        var sum = 0.0
        val n = a.size
        for (i in 1..<n) {
            sum += a[i]
        }
        val x: Array<Double> = Array(n) { 0.0 }
        x[0] = sum / a[0]
        for (i in 1..<n) {
            x[i] = a[i] / x[0]
        }
        return ArrayList(x.toList())
    }

    fun makeVideo() {
        if (controlPoints.size < 2) {
            println("Here should be at least 2 key frames")
            return
        }
        printKeyFrames()

        val width = controlPoints[0].width * 2
        val height = controlPoints[0].height * 2

        val fp = fpp.copy()

        val frames = (T * fps).toInt()
        val n = controlPoints.size
        frameInd.add(0)
        for (i in 1..<n - 1) {
            val dX1 = controlPoints[i].xSize - controlPoints[i - 1].xSize
            val dX2 = controlPoints[i + 1].xSize - controlPoints[i].xSize
            //println("dx1:" + dX1)
            //println("dx2:" + dX2)
            if (type(dX1) != type(dX2)) {
                frameInd.add(i)
                segmentType.add(type(dX1))
            }
        }
        val dX = controlPoints[n - 1].xSize - controlPoints[n - 2].xSize
        segmentType.add(type(dX))
        frameInd.add(n - 1)
        val xsizes = mutableListOf<Double>()
        val ysizes = mutableListOf<Double>()
        val xcorner = mutableListOf<Double>()
        val ycorner = mutableListOf<Double>()
        for (cp in controlPoints) {
            xsizes.add(cp.xSize)
            ysizes.add(cp.ySize)
            xcorner.add(cp.xMin)
            ycorner.add(cp.yMin)
        }
        calcCoeff(xsizes.toTypedArray(), ysizes.toTypedArray(), xcorner.toTypedArray(), ycorner.toTypedArray())

        val Xapprox = findApprox(xsizes.toTypedArray(), xcorner.toTypedArray())
        val Yapprox = findApprox(ysizes.toTypedArray(), ycorner.toTypedArray())

        val encoder = EncodeVideo(outputFileName, "mp4", width, height)
        val timeSource = TimeSource.Monotonic
        val mark1 = timeSource.markNow()
        printList(Xapprox.segmentT)
        printList(Yapprox.segmentT)
        //printList(segmentType)
        fp.maxIteration = 500
        var work = true
        val frameQueue = LinkedBlockingQueue<BufferedImage>()
        val usedFrames = LinkedBlockingQueue<BufferedImage>()
        val renderTh = thread {
            for (f in 0..frames) {
                val mk1 = timeSource.markNow()
                val t = f / (fps.toDouble())
                // find segment where placed t
                Xapprox.find(t)
                Yapprox.find(t)

                val dx = Xapprox.size
                val xx = Xapprox.corner

                val dy = Yapprox.size
                val yy = Yapprox.corner

                val p = Plane(xx, xx + dx, yy, yy + dy, width, height)
                fp.plane = p
                fp.previous_img = null
                val buff = if (usedFrames.isNotEmpty()) {
                    usedFrames.poll()
                } else {
                    BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
                }
                fp.fullPaint(buff, 0, 0)
                println("формирование кадра:F${f}")
                val mk2 = timeSource.markNow()
                println("render time: " + (mk2 - mk1))
                frameQueue.add(buff)
            }
            work = false
        }

        var f = 0
        val encoderTh = thread {
            while (work || frameQueue.isNotEmpty()) {
                if (frameQueue.isNotEmpty()) {
                    val buff = frameQueue.poll()
                    val mark11 = timeSource.markNow()
                    encoder.encodeFrame(buff)
                    val mark12 = timeSource.markNow()
                    usedFrames.add(buff)
                    println("encode frame:F${f}")
                    println("encode time: " + (mark12 - mark11))
                    f++
                }
            }
        }

        renderTh.join()
        encoderTh.join()

        encoder.close()
        val mark2 = timeSource.markNow()
        println("Время рендера: " + (mark2 - mark1))
        ////// RESET
        segmentType.clear()
        frameInd.clear()
        coefLists.clear()
        coeff.clear()
    }
}