package moe.nea.shale.dsl

import moe.nea.shale.layout.Element

@ShaleDsl
abstract class ShaleBuilder {
    var isFinalized = false
        private set

    open fun checkFinalize() {
        require(!isFinalized) { "Cannot modify element after it is finalized" }
    }

    fun markFinalized() {
        isFinalized = true
    }

    abstract fun compile(): Element
}
