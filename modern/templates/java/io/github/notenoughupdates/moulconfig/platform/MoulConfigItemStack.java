package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.IItemStack;
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import lombok.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@Value
@NullMarked
public class MoulConfigItemStack implements IItemStack {
    ItemStack itemStack;


    @Override
    public List<StructuredText> getLore() {
        return itemStack.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL)
            .stream()
            .map(MoulConfigPlatform::wrap)
            .toList();
    }

    @Override
    public StructuredText getDisplayName() {
        return MoulConfigPlatform.wrap(itemStack.getStyledHoverName());
    }

    @Override
    public int getStackSize() {
        return itemStack.getCount();
    }

    @Override
    public MyResourceLocation getItemId() {
        return MoulConfigPlatform.wrap(BuiltInRegistries.ITEM.getKey(itemStack.getItem()));
    }
}
