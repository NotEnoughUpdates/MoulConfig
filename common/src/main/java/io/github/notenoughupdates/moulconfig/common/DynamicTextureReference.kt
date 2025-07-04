package io.github.notenoughupdates.moulconfig.common

import org.jetbrains.annotations.ApiStatus
import java.awt.image.BufferedImage
import java.io.Closeable

/**
 * A dynamically loaded texture. Must be destroyed manually.
 */
@ApiStatus.NonExtendable
abstract class DynamicTextureReference : Closeable {
    /**
     * An opaque reference to this dynamic texture. Can be used with [RenderContext.drawTexturedRect].
     */
    abstract val identifier: MyResourceLocation

    /**
     * Destroy this texture. Using [identifier] after calling this will cause issues.
     */
    fun destroy() {
        if (!wasDestroyed)
            doDestroy()
        wasDestroyed = true
    }

    abstract fun update(bufferedImage: BufferedImage) // TODO: check why this is broken on 1.21

    protected abstract fun doDestroy()

    private var wasDestroyed = false

    override fun close() {
        destroy()
    }

    protected fun finalize() { // TODO: replace with a reference queue and Warnings.warn
        if (!wasDestroyed)
            IMinecraft.instance.getLogger("DynamicTextureReference").warn("Dangling DynamicTextureReference")
    }
}
