package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.common.MouseConstants;

public final class ForgeMouseConstants implements MouseConstants {
    protected static final ForgeMouseConstants INSTANCE = new ForgeMouseConstants();

    private ForgeMouseConstants() {}

    @Override
    public int left() {
        return 0;
    }

    @Override
    public int middle() {
        return 2;
    }

    @Override
    public int right() {
        return 1;
    }
}
