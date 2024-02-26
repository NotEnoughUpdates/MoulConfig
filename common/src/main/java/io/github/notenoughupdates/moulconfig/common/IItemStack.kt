package io.github.notenoughupdates.moulconfig.common

/**
 * Not for manual implementation. This should be implemented by the corresponding platform.
 *
 * @see io.github.notenoughupdates.moulconfig.forge.ForgeItemStack
 */
interface IItemStack {
    fun getLore(): List<String>
    fun getDisplayName(): String

    fun getStackSize(): Int
    fun getItemId(): MyResourceLocation
}
