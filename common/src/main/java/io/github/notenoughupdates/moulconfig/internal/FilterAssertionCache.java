package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.common.TextureFilter;
import lombok.Value;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static io.github.notenoughupdates.moulconfig.internal.StackUtil.MOULCONFIG_BASE_PACKAGE;

/**
 * This class is used to assert that the filter used for a texture (which is stored in global state) is never changed.
 */
public class FilterAssertionCache {
    @Value
    public static class TextureFilterAssertion {
        @Nullable StackTraceElement assertedBy;
        @NotNull TextureFilter filter;
    }

    private static final Map<MyResourceLocation, TextureFilterAssertion> PERMANENT = new HashMap<>();
    private static final Map<MyResourceLocation, TextureFilterAssertion> TEMPORARY = new HashMap<>();

    /**
     * Delete the global filter state for a texture. Should only be used if the texture itself ceased existing, not to change the filter of a texture.
     */
    public static void destroyGlobalFilter(MyResourceLocation resourceLocation) {
        PERMANENT.remove(resourceLocation);
        TEMPORARY.remove(resourceLocation);
    }

    /**
     * Assert that a texture uses a certain global filter state. If this is the first time the texture is seen, the state is remembered in this class. On subsequent calls this function warns if a different filter is passed.
     */
    public static void assertTextureFilter(MyResourceLocation resourceLocation, TextureFilter filter) {
        val set = IMinecraft.instance.isGeneratedSentinel(resourceLocation)
            ? TEMPORARY
            : PERMANENT;
        val existing = set.get(resourceLocation);
        Supplier<StackUtil> stack = () -> StackUtil.getWalker().skipWhile(
            StackUtil.defaultSkips()
                .or(it ->
                    !it.getClassName().startsWith(MOULCONFIG_BASE_PACKAGE + ".internal.")
                        && !it.getClassName().startsWith(MOULCONFIG_BASE_PACKAGE + ".platform.")));
        if (existing == null) {
            set.put(resourceLocation, new TextureFilterAssertion(stack.get().takeOne(), filter));
        } else if (existing.getFilter() != filter) {
            stack.get().warn("setting filter to " + filter + " despite filter originally being set to " + existing.getFilter() + " by " + existing.getAssertedBy());
        }
    }

}
