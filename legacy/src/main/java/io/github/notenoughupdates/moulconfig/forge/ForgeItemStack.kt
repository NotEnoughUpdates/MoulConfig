package io.github.notenoughupdates.moulconfig.forge

import io.github.notenoughupdates.moulconfig.common.IItemStack
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import io.github.notenoughupdates.moulconfig.internal.ForgeMinecraft
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class ForgeItemStack private constructor(val backing: ItemStack) : IItemStack {
    override fun getLore(): List<StructuredText> {
        return backing.getTooltip(Minecraft.getMinecraft().thePlayer, false)
            .map { StructuredText.of(it) }
    }

    override fun getDisplayName(): StructuredText {
        return StructuredText.of(backing.displayName)
    }

    override fun getStackSize(): Int {
        return backing.stackSize
    }

    override fun getItemId(): MyResourceLocation {
        return ForgeMinecraft.fromResourceLocation(Item.itemRegistry.getNameForObject(backing.item))
    }

    companion object {
        @JvmStatic
        fun of(itemStack: ItemStack): IItemStack {
            return ForgeItemStack(itemStack)
        }
    }

}
