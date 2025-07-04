package io.github.notenoughupdates.moulconfig.common

import org.jetbrains.annotations.ApiStatus

/**
 * Not for manual implementation. This should be implemented by the corresponding platform.
 *
 * @see io.github.notenoughupdates.moulconfig.forge.ForgeItemStack
 */
@ApiStatus.NonExtendable
interface IItemStack {
    fun getLore(): List<String>
    fun getDisplayName(): String

    fun getStackSize(): Int
    fun getItemId(): MyResourceLocation
}
