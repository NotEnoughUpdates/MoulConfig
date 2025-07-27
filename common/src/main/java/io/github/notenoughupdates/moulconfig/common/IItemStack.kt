package io.github.notenoughupdates.moulconfig.common

import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import org.jetbrains.annotations.ApiStatus

/**
 * Not for manual implementation. This should be implemented by the corresponding platform.
 *
 * @see io.github.notenoughupdates.moulconfig.forge.ForgeItemStack
 */
@ApiStatus.NonExtendable
interface IItemStack {
    fun getLore(): List<StructuredText>
    fun getDisplayName(): StructuredText

    fun getStackSize(): Int
    fun getItemId(): MyResourceLocation
}
