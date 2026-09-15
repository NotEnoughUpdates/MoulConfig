package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.MouseConstants;
import com.mojang.blaze3d.platform.InputConstants;

public final class ModernMouseConstants implements MouseConstants {
    protected static final ModernMouseConstants INSTANCE = new ModernMouseConstants();

    private ModernMouseConstants() {}

    @Override
    public int left() {
        return InputConstants.MOUSE_BUTTON_LEFT;
    }

    @Override
    public int middle() {
        return InputConstants.MOUSE_BUTTON_MIDDLE;
    }

    @Override
    public int right() {
        return InputConstants.MOUSE_BUTTON_RIGHT;
    }
}
