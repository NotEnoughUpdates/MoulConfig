package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class StackUtil {
    private final StackTraceElement[] stackTrace;
    private int offset;

    public StackUtil(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }

    public StackUtil skipWhile(Predicate<StackTraceElement> filter) {
        while (stackTrace.length > offset && filter.test(stackTrace[offset])) {
            offset++;
        }
        return this;
    }

    public StackUtil skip(int count) {
        offset = Math.min(offset + count, stackTrace.length);
        return this;
    }

    public @Nullable StackTraceElement takeOne() {
        if (offset >= stackTrace.length) {
            return null;
        }
        return stackTrace[offset++];
    }

    public void warn(String warningText) {
        val firstOffender = takeOne();
        assert firstOffender != null;
        Warnings.warnAt(warningText, firstOffender, skipWhile(moulConfigLibrary()).takeOne());
    }


    public static Predicate<StackTraceElement> inTests() {
        return it -> it.getClassName().startsWith(MOULCONFIG_BASE_PACKAGE + ".test.");
    }

    public static Predicate<StackTraceElement> anyMoulConfig() {
        return it -> it.getClassName().startsWith(MOULCONFIG_BASE_PACKAGE + ".");
    }

    public static Predicate<StackTraceElement> moulConfigLibrary() {
        return anyMoulConfig().and(inTests().negate());
    }

    public static Predicate<StackTraceElement> defaultSkips() {
        return it ->
            it.getClassName().equals(StackUtil.class.getName())
                || it.getClassName().equals(Warnings.class.getName())
                || it.getClassName().startsWith("java.")
                || it.getClassName().startsWith("kotlin.");
    }

    public static final String MOULCONFIG_BASE_PACKAGE = GuiTextures.class.getPackage().getName();

    public static StackUtil getWalker() {
        return new StackUtil(new Exception().getStackTrace()).skip(1);
    }
}
