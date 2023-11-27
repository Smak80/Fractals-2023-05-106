package math

import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.sqrt

class Complex(var re: Double = 0.0, var im: Double = 0.0) {
    fun abs() = sqrt(re * re + im * im)
    fun abs2() = re * re + im * im
    fun arg() = atan2(im, re)
    operator fun plus(other: Complex) = Complex(re + other.re, im + other.im)
    operator fun minus(other: Complex) = Complex(re - other.re, im - other.im)
    operator fun times(other: Complex) = Complex(re * other.re - im * other.im, re * other.im + other.re * im)
    operator fun div(other: Complex) = (other.re * other.re + other.im * other.im).let{Complex((re * other.re + im * other.im) / it,(other.re * im - re * other.im) / it)}

    operator fun plusAssign(other: Complex){
        re += other.re
        im += other.im
    }
    operator fun minusAssign(other: Complex){
        re -= other.re
        im -= other.im
    }

    operator fun timesAssign(other: Complex){
        val r = re * other.re - im * other.im
        im = re * other.im + other.re * im
        re = r
    }

    operator fun divAssign(other: Complex){
        val r = (re * other.re + im * other.im) / other.abs2()
        im = (other.re * im - re * other.im) / other.abs2()
        re = r
    }

    override fun toString() = buildString {
        if(re==0.0 && im==0.0) append("0")
        if(re!=0.0) append(re)
        if(im!=0.0){
            append(if(im<0.0) "-" else if(re!=0.0) "+" else "")
            if(abs(im)!=1.0) append(abs(im))
            append("i")
        }
    }
}