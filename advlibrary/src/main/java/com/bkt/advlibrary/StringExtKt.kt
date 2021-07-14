package com.bkt.advlibrary

fun String.getWords(): List<String> {
    return split(" ",".")
}

fun String.isNumber(): Boolean {
    return try {
        this.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun String.search(text: String): Boolean {
    return this.contains(text, ignoreCase = true)
}