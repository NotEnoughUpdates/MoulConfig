package io.github.notenoughupdates.moulconfig.common;

import org.jetbrains.annotations.ApiStatus;

import java.awt.image.BufferedImage;
import java.io.Closeable;

/**
 * A dynamically loaded texture. Must be destroyed manually.
 */
@ApiStatus.NonExtendable
public abstract class DynamicTextureReference implements Closeable {
    private boolean wasDestroyed;

    /**
     * An opaque reference to this dynamic texture. Can be used with {@link RenderContext#drawTexturedRect}.
     */
    public abstract MyResourceLocation getIdentifier();

    /**
     * Destroy this texture. Using {@link #getIdentifier()} after calling this will cause issues.
     */
    public final void destroy() {
        if (!wasDestroyed) {
            doDestroy();
        }
        wasDestroyed = true;
    }

    public abstract void update(BufferedImage bufferedImage);

    protected abstract void doDestroy();

    @Override
    public final void close() {
        destroy();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (!wasDestroyed) {
                IMinecraft.INSTANCE.getLogger("DynamicTextureReference").warn("Dangling DynamicTextureReference");
            }
        } finally {
            super.finalize();
        }
    }
}
