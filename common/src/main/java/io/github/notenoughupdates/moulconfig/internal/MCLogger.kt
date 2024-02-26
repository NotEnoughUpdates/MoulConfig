package io.github.notenoughupdates.moulconfig.internal

interface MCLogger {
    fun warn(text: String)
    fun info(text: String)
    fun error(text: String, throwable: Throwable)
}