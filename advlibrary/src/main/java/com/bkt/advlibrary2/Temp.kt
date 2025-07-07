package com.bkt.advlibrary

sealed class Test {
    object nice : Test()
}

data class awesome(val name: String) : Test()