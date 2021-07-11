package com.bkt.advlibrary

import java.util.*

fun String.getWords(): ArrayList<String> {
    return CommonFunctions.StringUtils.getWords(this)
}

fun String.isNumber(): Boolean {
    return try {
        this.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}