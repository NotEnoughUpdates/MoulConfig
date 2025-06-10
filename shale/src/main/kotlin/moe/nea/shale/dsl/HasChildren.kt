package moe.nea.shale.dsl

import moe.nea.shale.layout.Element

@ShaleDsl
interface HasChildren<T : Element> {
    fun add(element: T)

}
