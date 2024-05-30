package com.bkt.advlibrary

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*
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

fun Double?.roundToNearest(value: Int): Double {
    return ((this ?: 0.0) / value.toDouble()).roundToLong() * value.toDouble()
}

fun BigDecimal.roundedText(maxRadix: Int): String {
    val amountDecimal = remainder(BigDecimal.ONE)
    return if (amountDecimal.compareTo(BigDecimal(0.0)) == 0)
        setScale(0, RoundingMode.HALF_UP).toString()
    else setScale(kotlin.math.min(scale(), maxRadix), RoundingMode.HALF_UP).toString()
}

fun BigDecimal.toCurrency(): String {
    val text =
        NumberFormat.getCurrencyInstance(Locale("en", "in")).format(this.stripTrailingZeros())
    return when {
        text.endsWith(".00") -> text.replace(".00", "")
        else -> text
    }
}

fun BigDecimal.toText(): String {
    return stripTrailingZeros().toPlainString()
}

fun BigDecimal.toText(roundedTo: Int): String {
    return setScale(roundedTo, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString()
}

fun BigDecimal.compareTo(value: Double): Int {
    val compareValue = BigDecimal.valueOf(value)
    return this.compareTo(compareValue)
}

fun BigDecimal.getPercentageTo(target: BigDecimal, scale: Int = 2): BigDecimal {
    if (this == BigDecimal.ZERO)
        return BigDecimal.ZERO
    val diff = target - this
    return (diff * BigDecimal(100)).divide(this, scale, RoundingMode.HALF_EVEN)
}

fun BigDecimal.getPercentageOf(target: BigDecimal, scale: Int = 2): BigDecimal {
    if (target == BigDecimal.ZERO)
        return BigDecimal.ZERO
    return (this * BigDecimal(100)).divide(target, scale, RoundingMode.HALF_EVEN)
}

fun String?.toBigDecimalOr(value: Int): BigDecimal {
    if (this == null || this.isEmpty())
        return BigDecimal(value)
    return BigDecimal(this)
}