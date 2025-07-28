package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.IItemStack;
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import lombok.Value;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@Value
@NullMarked
public class MoulConfigItemStack implements IItemStack {
    ItemStack itemStack;


    @Override
    public List<StructuredText> getLore() {
        return itemStack.getTooltip(Item.TooltipContext.DEFAULT, MinecraftClient.getInstance().player, TooltipType.BASIC)
            .stream()
            .map(MoulConfigPlatform::wrap)
            .toList();
    }

    @Override
    public StructuredText getDisplayName() {
        return MoulConfigPlatform.wrap(itemStack.getFormattedName());
    }

    @Override
    public int getStackSize() {
        return itemStack.getCount();
    }

    @Override
    public MyResourceLocation getItemId() {
        return MoulConfigPlatform.wrap(Registries.ITEM.getId(itemStack.getItem()));
    }
}
