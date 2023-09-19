package io.github.moulberry.moulconfig.forge

import io.github.moulberry.moulconfig.common.IItemStack
import io.github.moulberry.moulconfig.common.MyResourceLocation
import io.github.moulberry.moulconfig.internal.ForgeMinecraft
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class ForgeItemStack private constructor(val backing: ItemStack) : IItemStack {
    override fun getLore(): List<String> {
        return backing.getTooltip(Minecraft.getMinecraft().thePlayer, false)
    }

    override fun getDisplayName(): String {
        return backing.displayName
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