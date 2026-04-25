package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;

public final class DrawContextExt {
    public static final DrawContextExt INSTANCE = new DrawContextExt();

    private DrawContextExt() {
    }

    public static void drawStringCenteredScalingDownWithMaxWidth(
        RenderContext context,
        StructuredText text,
        int centerX,
        int centerY,
        int maxWidth,
        int color
    ) {
        drawStringCenteredScalingDownWithMaxWidth(context, text, centerX, centerY, maxWidth, color, false, context.getMinecraft().getDefaultFontRenderer());
    }

    public static void drawStringCenteredScalingDownWithMaxWidth(
        RenderContext context,
        StructuredText text,
        int centerX,
        int centerY,
        int maxWidth,
        int color,
        boolean shadow
    ) {
        drawStringCenteredScalingDownWithMaxWidth(context, text, centerX, centerY, maxWidth, color, shadow, context.getMinecraft().getDefaultFontRenderer());
    }

    public static void drawStringCenteredScalingDownWithMaxWidth(
        RenderContext context,
        StructuredText text,
        int centerX,
        int centerY,
        int maxWidth,
        int color,
        boolean shadow,
        IFontRenderer fr
    ) {
        context.pushMatrix();
        int width = fr.getStringWidth(text);
        float factor = Math.min(maxWidth / (float) width, 1F);
        context.translate((float) centerX, (float) centerY);
        context.scale(factor, factor);
        context.drawString(fr, text, -width / 2, -fr.getHeight() / 2, color, shadow);
        context.popMatrix();
    }
}
