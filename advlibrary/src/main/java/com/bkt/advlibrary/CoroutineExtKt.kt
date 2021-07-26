package com.bkt.advlibrary

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.timerTask


private val bgScope = CoroutineScope(IO)
private val mainScope = CoroutineScope(Main)
private val monoScope by lazy {
    CoroutineScope(
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    )
}

fun bgLaunch(block: () -> Unit) {
    bgScope.launch {
        block.invoke()
    }
}

fun mainLaunch(block: () -> Unit) {
    mainScope.launch {
        block.invoke()
    }
}

fun monoLaunch(block: () -> Unit) {
    monoScope.launch {
        block.invoke()
    }
}

fun after(milliSecs: Long, block: () -> Unit) {
    Timer().schedule(timerTask { block.invoke() }, milliSecs)
}

fun <T> bgBlock(block: suspend CoroutineScope.() -> T): T {
    return runBlocking { block.invoke(bgScope) }
}

fun <T> mainBlock(block: suspend CoroutineScope.() -> T): T {
    return runBlocking { block.invoke(mainScope) }
}