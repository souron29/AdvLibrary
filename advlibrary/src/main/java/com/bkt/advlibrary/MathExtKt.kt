package library.extensions

import kotlin.math.roundToLong

fun Int.pow(exp: Int): Int {
    var res = 1
    for (i in exp downTo 1) {
        res *= this
    }
    return res
}

fun Double.pow(exp: Int): Double {
    var res = 1.0
    for (i in exp downTo 1) {
        res *= this
    }
    return res
}

fun Float.pow(exp: Int): Float {
    var res = 1.0f
    for (i in exp downTo 1) {
        res *= this
    }
    return res
}

fun Double.round(radix: Int): Double {
    val value = this * 10.0.pow(radix)
    val tmp = value.roundToLong()
    return tmp.toDouble() / 10.0.pow(radix)
}