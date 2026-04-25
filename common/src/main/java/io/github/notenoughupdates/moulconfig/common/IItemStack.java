package io.github.notenoughupdates.moulconfig.common;

import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Not for manual implementation. This should be implemented by the corresponding platform.
 *
 * @see <a href="../../../../../../legacy/io/github/notenoughupdates/moulconfig/forge/ForgeItemStack.html">ForgeItemStack</a>
 */
@ApiStatus.NonExtendable
public interface IItemStack {
    List<StructuredText> getLore();

    StructuredText getDisplayName();

    int getStackSize();

    MyResourceLocation getItemId();
}
