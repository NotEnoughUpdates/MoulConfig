package io.github.moulberry.moulconfig.mixin;

import io.github.moulberry.moulconfig.test.MoulConfigTest;
import io.github.moulberry.moulconfig.test.TestCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class RenderHudMixin {

    @Unique
    private TestCategory.TestOverlay o = MoulConfigTest.testConfig.testCategory.testOverlay;

    @Inject(method="render", at=@At(
            value="FIELD",
            target="Lnet/minecraft/client/option/GameOptions;debugEnabled:Z",
            opcode = Opcodes.GETFIELD, args = {"log=false"}))
    private void afterStatusEffects(DrawContext context, float tickDelta, CallbackInfo ci) {
        context.getMatrices().push();
        o.transform(context);
        context.fill(0, 0, 200, 100, 0xFFFFFFFF);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        double x = MinecraftClient.getInstance().mouse.getX();
        double y = MinecraftClient.getInstance().mouse.getY();
        int i = 0;
        int color = 0xFFA4A4A4;
        double mx = (MinecraftClient.getInstance().mouse.getX() * (double)MinecraftClient.getInstance().getWindow().getScaledWidth() / (double)MinecraftClient.getInstance().getWindow().getWidth());
        double my = (MinecraftClient.getInstance().mouse.getY() * (double)MinecraftClient.getInstance().getWindow().getScaledHeight() / (double)MinecraftClient.getInstance().getWindow().getHeight());
        context.drawTextWithShadow(textRenderer, Text.literal("Global Mouse X: " + x).formatted(Formatting.BOLD), 10, ++i * 10, color);
        context.drawTextWithShadow(textRenderer, Text.literal("Global Mouse Y: " + y).formatted(Formatting.BOLD), 10, ++i * 10, color);
        context.drawTextWithShadow(textRenderer, Text.literal("Local Mouse X: " + o.realWorldXToLocalX((float) x)).formatted(Formatting.BOLD), 10, ++i * 10, color);
        context.drawTextWithShadow(textRenderer, Text.literal("Local Mouse Y: " + o.realWorldYToLocalY((float) y)).formatted(Formatting.BOLD), 10, ++i * 10, color);
        context.drawTextWithShadow(textRenderer, Text.literal("Scaled Mouse X: %.5f".formatted(mx)).formatted(Formatting.BOLD), 10, ++i * 10, color);
        context.drawTextWithShadow(textRenderer, Text.literal("Scaled Mouse Y: %.5f".formatted(my)).formatted(Formatting.BOLD), 10, ++i * 10, color);
        context.drawTextWithShadow(textRenderer, Text.literal("Width: " + o.getWidth()).formatted(Formatting.BOLD), 10, ++i * 10, color);
        context.drawTextWithShadow(textRenderer, Text.literal("Height: " + o.getHeight()).formatted(Formatting.BOLD), 10, ++i * 10, color);
        context.getMatrices().pop();
    }

}
