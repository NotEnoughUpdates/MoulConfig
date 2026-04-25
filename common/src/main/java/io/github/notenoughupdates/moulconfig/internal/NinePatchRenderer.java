package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import juuxel.libninepatch.ContextualTextureRenderer;

public final class NinePatchRenderer implements ContextualTextureRenderer<MyResourceLocation, RenderContext> {
    public static final NinePatchRenderer INSTANCE = new NinePatchRenderer();

    private NinePatchRenderer() {
    }

    @Override
    public void draw(MyResourceLocation texture, RenderContext context, int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
        context.drawComplexTexture(texture, (float) x, (float) y, (float) width, (float) height, draw -> draw.uv(u1, v1, u2, v2));
    }
}
