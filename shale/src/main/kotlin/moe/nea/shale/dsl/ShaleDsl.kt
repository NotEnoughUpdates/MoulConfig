package moe.nea.shale.dsl

import moe.nea.shale.layout.RootElement

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class ShaleDsl

fun buildShaleLayout(builder: ShaleRootBuilder.() -> Unit): RootElement {
    return ShaleRootBuilder()
        .also(builder)
        .compile()
}
