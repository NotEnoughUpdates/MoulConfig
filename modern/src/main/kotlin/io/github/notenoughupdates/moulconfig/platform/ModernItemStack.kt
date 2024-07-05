package io.github.notenoughupdates.moulconfig.platform

import io.github.notenoughupdates.moulconfig.common.IItemStack
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.Registries
import net.minecraft.world.World

class ModernItemStack private constructor(val backing: ItemStack) : IItemStack {
    override fun getLore(): List<String> {
        return backing.getTooltip(Item.TooltipContext.create(null as World?), MinecraftClient.getInstance().player, TooltipType.BASIC).map { it.string }
    }

    override fun getDisplayName(): String {
        return backing.name.string
    }

    override fun getStackSize(): Int {
        return backing.count
    }

    override fun getItemId(): MyResourceLocation {
        return ModernMinecraft.fromIdentifier(Registries.ITEM.getId(backing.item))
    }

    companion object {
        @JvmStatic
        fun of(itemStack: ItemStack): IItemStack {
            return ModernItemStack(itemStack)
        }
    }
}
