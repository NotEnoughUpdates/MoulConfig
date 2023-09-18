package io.github.moulberry.moulconfig.gui.component;

import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.internal.RenderUtils;
import io.github.moulberry.moulconfig.observer.GetSetter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@RequiredArgsConstructor
public class SliderComponent extends GuiComponent {

    final GetSetter<Float> value;
    final float minValue;
    final float maxValue;
    final float minStep;
    final int width;
    boolean clicked;

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return 16;
    }


    @Override
    public void render(GuiImmediateContext context) {
        if (clicked) {
            setValueFromContext(context);
        }
        float value = this.value.get();
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(GuiTextures.SLIDER_ON_CAP);
        RenderUtils.drawTexturedRect(0, 0, 4, getHeight(), GL11.GL_NEAREST);
        mc.getTextureManager().bindTexture(GuiTextures.SLIDER_OFF_CAP);
        RenderUtils.drawTexturedRect(width - 4, 0, 4, getHeight(), GL11.GL_NEAREST);

        int sliderPosition = (int) (value / (maxValue - minValue) * width);

        if (sliderPosition > 5) {
            mc.getTextureManager().bindTexture(GuiTextures.SLIDER_ON_SEGMENT);
            RenderUtils.drawTexturedRect(4, 0, sliderPosition - 4, getHeight(), GL11.GL_NEAREST);
        }

        if (sliderPosition < width - 5) {
            mc.getTextureManager().bindTexture(GuiTextures.SLIDER_OFF_SEGMENT);
            RenderUtils.drawTexturedRect(sliderPosition, 0, width - 4 - sliderPosition, getHeight(), GL11.GL_NEAREST);
        }

        for (int i = 0; i < 4; i++) {
            int notchX = width * i / 4 - 1;
            mc.getTextureManager().bindTexture(notchX > sliderPosition ? GuiTextures.SLIDER_OFF_NOTCH : GuiTextures.SLIDER_ON_NOTCH);
            RenderUtils.drawTexturedRect(notchX, (getHeight() - 4) / 2, 2, 4, GL11.GL_NEAREST);
        }

        mc.getTextureManager().bindTexture(GuiTextures.SLIDER_BUTTON);
        RenderUtils.drawTexturedRect(sliderPosition - 4, 0, 8, getHeight(), GL11.GL_NEAREST);
    }

    public void setValueFromContext(GuiImmediateContext context) {
        float v = context.getMouseX() * (maxValue - minValue) / width;
        v = Math.min(v, maxValue);
        v = Math.max(v, minValue);
        v = Math.round(v / minStep) * minStep;
        value.set(v);
    }

    @Override
    public void mouseEvent(GuiImmediateContext context) {
        if (!Mouse.isButtonDown(0))
            clicked = false;
        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && context.isHovered()) {
            clicked = true;
        }
        if (clicked) {
            setValueFromContext(context);
        }
    }
}
