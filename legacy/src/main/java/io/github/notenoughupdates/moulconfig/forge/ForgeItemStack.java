package io.github.notenoughupdates.moulconfig.forge;

import io.github.notenoughupdates.moulconfig.common.IItemStack;
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.internal.ForgeMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ForgeItemStack implements IItemStack {
    private final ItemStack backing;

    private ForgeItemStack(ItemStack backing) {
        this.backing = backing;
    }

    public ItemStack getBacking() {
        return backing;
    }

    @Override
    public List<StructuredText> getLore() {
        List<StructuredText> result = new ArrayList<>();
        for (String line : backing.getTooltip(Minecraft.getMinecraft().thePlayer, false)) {
            result.add(StructuredText.of(line));
        }
        return result;
    }

    @Override
    public StructuredText getDisplayName() {
        return StructuredText.of(backing.getDisplayName());
    }

    @Override
    public int getStackSize() {
        return backing.stackSize;
    }

    @Override
    public MyResourceLocation getItemId() {
        return ForgeMinecraft.fromResourceLocation(Item.itemRegistry.getNameForObject(backing.getItem()));
    }

    public static IItemStack of(ItemStack itemStack) {
        return new ForgeItemStack(itemStack);
    }
}
