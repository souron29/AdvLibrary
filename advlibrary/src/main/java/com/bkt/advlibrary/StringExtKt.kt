package com.bkt.advlibrary

import java.util.*

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

val String.capitalizeWords
    get() = this.lowercase(Locale.getDefault()).split(" ")
        .joinToString(" ") { it -> it.replaceFirstChar { innerIt -> innerIt.titlecase() } }
