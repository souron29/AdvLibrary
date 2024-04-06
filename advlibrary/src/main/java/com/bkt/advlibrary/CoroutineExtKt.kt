package com.bkt.advlibrary

import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.timerTask


private val bgScope = CoroutineScope(Dispatchers.Default)
private val mainScope = CoroutineScope(Dispatchers.Main)

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

fun <T> runInBg(block: suspend CoroutineScope.() -> T): Job {
    return bgScope.launch {
        block.invoke(this)
    }
}

fun <T> runInMain(block: suspend CoroutineScope.() -> T): Job {
    return mainScope.launch {
        block.invoke(this)
    }
}

/**
 * Use this for a running transactions inside a single
 */
fun monoLaunch(block: () -> Unit) {
    monoScope.launch {
        block.invoke()
    }
}

/**
 * Use this inside a mainLaunch to perform transactions in parallel and use the result on the UI thread
 */
fun <T> bgSeries(block: () -> T): T {
    return runBlocking {
        withContext(bgScope.coroutineContext) { block.invoke() }
    }
}

fun <T> bgAsync(block: () -> T): Deferred<T> {
    return bgScope.async { block.invoke() }
}

fun after(milliSecs: Long, block: () -> Unit) {
    runInBg {
        delay(milliSecs)
        mainLaunch(block)
    }
}

fun <T> bgBlock(block: suspend CoroutineScope.() -> T): T {
    return runBlocking { block.invoke(bgScope) }
}

fun <T> mainBlock(block: suspend CoroutineScope.() -> T): T {
    return runBlocking { block.invoke(mainScope) }
}
