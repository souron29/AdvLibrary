package library

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.concurrent.timerTask


private val bgScope = CoroutineScope(IO)
private val mainScope = CoroutineScope(Main)

fun bgLaunch(block: () -> Unit): Job {
    return bgScope.launch {
        block.invoke()
    }
}

fun mainLaunch(block: () -> Unit): Job {
    return mainScope.launch {
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