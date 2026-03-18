package io.github.notenoughupdates.moulconfig.platform;

#if MC >= 12111
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
#endif
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.notenoughupdates.moulconfig.common.*;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.internal.FilterAssertionCache;
import io.github.notenoughupdates.moulconfig.internal.Rect;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import lombok.Getter;
import lombok.Value;
import net.minecraft.client.Minecraft;
#if MC < 260100
import net.minecraft.client.gui.GuiGraphics;
#else
import net.minecraft.client.gui.GuiGraphicsExtractor;
#endif
import net.minecraft.client.gui.navigation.ScreenRectangle;
#if MC < 12111
import net.minecraft.client.renderer.RenderType;
#endif
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.locale.Language;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
#if MC217
import net.minecraft.client.gui.render.TextureSetup;
#if MC < 260100
import net.minecraft.client.gui.render.state.GuiElementRenderState;
#else
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
#endif
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
#endif

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Consumer;

@NullMarked
public class MoulConfigRenderContext implements RenderContext {
    @Getter
    final #if MC < 260100 GuiGraphics #else GuiGraphicsExtractor #endif drawContext;
    Minecraft mc = Minecraft.getInstance();

    public MoulConfigRenderContext(#if MC < 260100 GuiGraphics #else GuiGraphicsExtractor #endif drawContext) {
        this.drawContext = drawContext;
    }


    public #if MC217 Matrix3x2fStack #else PoseStack #endif getMatrices() {
        return drawContext.pose();
    }

    @Override
    public void pushMatrix() {
        #if MC217
            getMatrices().pushMatrix();
        #else
        getMatrices().pushPose();
        #endif
    }

    @Override
    public void popMatrix() {
        #if MC217
            getMatrices().popMatrix();
        #else
        getMatrices().popPose();
        #endif
    }

    @Override
    public void translate(float x, float y) {
        #if MC217
        getMatrices().translate(x, y);
        #else
        getMatrices().translate(x, y, 0);
        #endif
    }

    @Override
    public void scale(float x, float y) {
        #if MC217
        getMatrices().scale(x, y);
        #else
        getMatrices().scale(x, y, 1);
        #endif
    }

    #if MC217
    @Override
    public void drawOnTop(Layer layer, ScissorBehaviour escapeScissors, Consumer<RenderContext> later) {
        var queuedActions = queuedLayers.computeIfAbsent(layer, ignored -> new ArrayList<>());
        queuedActions.add(new DrawAction(
            later,
            switch (escapeScissors) {
                case ESCAPE -> null;
                case INHERIT -> drawContext.scissorStack.stack.peekLast();
            },
            new Matrix3x2f(getMatrices())));
    }
    #else
    @Override
    public void drawOnTop(Layer layer, ScissorBehaviour escapeScissors, Consumer<RenderContext> later) {
        pushMatrix();
        if (escapeScissors == ScissorBehaviour.ESCAPE) {
            pushRawScissor(0, 0, IMinecraft.INSTANCE.getScaledWidth(), IMinecraft.INSTANCE.getScaledHeight());
        }

        getMatrices().translate(0F, 0F, layer.getSortIndex());

        later.accept(this);

        if (escapeScissors == ScissorBehaviour.ESCAPE) {
            popScissor();
        }
        popMatrix();
    }
    #endif

    @Override
    public void drawColouredQuads(int color, float... coordinates) {
        assert coordinates.length % 8 == 0;
        var rect = Rect.ofDot((int) coordinates[0], (int) coordinates[1]);
        for (int i = 0; i < coordinates.length; i += 2) {
            rect = rect.includePoint((int) coordinates[i], (int) coordinates[i + 1]);
        }
        #if MC217
        var scissors = drawContext.scissorStack.stack.peekLast();
        var matrix = new Matrix3x2f(getMatrices());
        var bounds = new ScreenRectangle(rect.getX(), rect.getY(), rect.getW(), rect.getH());
        bounds = bounds.transformMaxBounds(matrix);
        if (scissors != null)
            bounds = bounds.intersection(scissors);
        if (bounds == null)
            return;
        var finalBounds = bounds;
        drawContext.guiRenderState.#if MC < 260100 submitGuiElement #else addGuiElement #endif(new GuiElementRenderState() {
            @Override
            public void buildVertices(VertexConsumer vertices #if MC < 12109 , float depth #endif) {
                for (int i = 0; i < coordinates.length; i += 2) {
                    vertices.addVertexWith2DPose(matrix, coordinates[i], coordinates[i + 1] #if MC < 12109 , depth #endif)
                        .setColor(color);
                }
            }

            @Override
            public RenderPipeline pipeline() {
                return RenderPipelines.GUI;
            }

            @Override
            public TextureSetup textureSetup() {
                return TextureSetup.noTexture();
            }

            @Override
            public @Nullable ScreenRectangle scissorArea() {
                return scissors;
            }

            @Override
            public ScreenRectangle bounds() {
                return finalBounds;
            }
        });
        #else
        drawContext.drawSpecial(consumers -> {
            var matrix = getMatrices().last().pose();
            var vertices = consumers.getBuffer(RenderType.gui());
            for (int i = 0; i < coordinates.length; i += 2) {
                vertices.addVertex(matrix, coordinates[i], coordinates[i + 1], 0F)
                    .setColor(color);
            }
        });
        #endif
    }

    @Override
    public void drawString(IFontRenderer fontRenderer, StructuredText text, int x, int y, int color, boolean shadow) {
        drawContext.#if MC < 260100 drawString #else text #endif(
            MoulConfigPlatform.unwrap(fontRenderer),
            MoulConfigPlatform.unwrap(text),
            x,
            y,
            color | 0xFF000000,
            shadow
        );
    }

    @Override
    public void drawColoredRect(float left, float top, float right, float bottom, int color) {
        drawContext.fill((int) left, (int) top, (int) right, (int) bottom, color);
    }

    @Override
    public void invertedRect(float left, float top, float right, float bottom, int additiveColor) {
        int leftI = (int) left;
        int topI = (int) top;
        int rightI = (int) right;
        int bottomI = (int) bottom;
        #if MC217
        #if MC < 260100
        drawContext.submitColoredRectangle(RenderPipelines.GUI_INVERT, TextureSetup.noTexture(), leftI, topI, rightI, bottomI, -1, null);
        drawContext.submitColoredRectangle(RenderPipelines.GUI_TEXT_HIGHLIGHT, TextureSetup.noTexture(), leftI, topI, rightI, bottomI, additiveColor, null);
        #else
        drawContext.innerFill(RenderPipelines.GUI_INVERT, TextureSetup.noTexture(), leftI, topI, rightI, bottomI, -1, null);
        drawContext.innerFill(RenderPipelines.GUI_TEXT_HIGHLIGHT, TextureSetup.noTexture(), leftI, topI, rightI, bottomI, additiveColor, null);
        #endif
        #else
        drawContext.fill(RenderType.guiTextHighlight(), leftI, topI, rightI, bottomI, 0, additiveColor);
        #endif
    }

    @Override
    public void drawTexturedTintedRect(MyResourceLocation texture, float x, float y, float width, float height, float u1, float v1, float u2, float v2, int color, TextureFilter filter) {
        FilterAssertionCache.assertTextureFilter(texture, filter);
        var identifier = MoulConfigPlatform.unwrap(texture);
        #if MC < 12111
        mc.getTextureManager()
            .getTexture(identifier)
            .setFilter(
                switch (filter) {
                    case LINEAR -> true;
                    case NEAREST -> false;
                },
                false
            );
        drawContext.innerBlit(
            #if MC217 RenderPipelines.GUI_TEXTURED #else RenderType::guiTextured #endif,
            identifier,
            (int) x, (int) (x + width), (int) y, (int) (y + height),
            u1, u2, v1, v2,
            color
        );
        #else
        FilterMode filterMode = switch (filter) {
            case TextureFilter.LINEAR -> FilterMode.LINEAR;
            case TextureFilter.NEAREST -> FilterMode.NEAREST;
        };
        drawContext.#if MC < 260100 submitBlit #else innerBlit #endif(
            RenderPipelines.GUI_TEXTURED,
            mc.getTextureManager().getTexture(identifier).getTextureView(),
            RenderSystem.getSamplerCache().getRepeat(filterMode),
            (int) x,
            (int) y,
            (int) (x + width),
            (int) (y + height),
            u1,
            u2,
            v1,
            v2,
            color
        );
        #endif
    }

    @Override
    public void drawDarkRect(int x, int y, int width, int height, boolean shadow) {
        var body = 0xff202026;
        var light = 0xff303036;
        var dark = 0xff101016;
        drawContext.fill(x, y, x + 1, y + height, light); //Left
        drawContext.fill(x + 1, y, x + width, y + 1, light); //Top
        drawContext.fill(x + width - 1, y + 1, x + width, y + height, dark); //Right
        drawContext.fill(x + 1, y + height - 1, x + width - 1, y + height, dark); //Bottom
        drawContext.fill(x + 1, y + 1, x + width - 1, y + height - 1, body); //Middle
        if (shadow) {
            drawContext.fill(x + width, y + 2, x + width + 2, y + height + 2, 0x70000000); //Right shadow
            drawContext.fill(x + 2, y + height, x + width, y + height + 2, 0x70000000); //Bottom shadow
        }
    }

    @Override
    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        drawContext.fillGradient(left, top, right, bottom, startColor, endColor);
    }

    @Override
    public void pushScissor(int left, int top, int right, int bottom) {
        drawContext.enableScissor(left, top, right, bottom);
    }

    @Override
    public void pushRawScissor(int left, int top, int right, int bottom) {
        drawContext.scissorStack.stack.addLast(new ScreenRectangle(left, top, right, bottom));
    }

    @Override
    public void popScissor() {
        drawContext.disableScissor();
    }

    @Override
    public void assertNoScissors() {
        if (!drawContext.scissorStack.stack.isEmpty())
            Warnings.warn("Scissors found despite no scissor assertion", 4);
    }

    @Override
    @Deprecated
    public void clearScissor() {
        drawContext.scissorStack.stack.clear();
    }

    @Override
    public void renderItemStack(IItemStack itemStack, int x, int y, @Nullable StructuredText overlayText) {
        var item = MoulConfigPlatform.unwrap(itemStack);
        drawContext.#if MC < 260100 renderItem #else item #endif(item, x, y);
        if (overlayText != null)
            drawContext.#if MC < 260100 renderItemDecorations #else itemDecorations #endif(
                mc.font,
                item,
                x, y,
                overlayText.getText()
            );
    }

    @Override
    public void drawTooltipNow(int x, int y, List<StructuredText> tooltipLines) {
        #if MC217
        var lines = tooltipLines.stream()
            .map(MoulConfigPlatform::unwrap)
            .map(Language.getInstance()::getVisualOrder)
            .map(ClientTooltipComponent::create)
            .toList();
        drawContext.#if MC < 260100 renderTooltip #else tooltip #endif(
            mc.font,
            lines,
            x, y,
            DefaultTooltipPositioner.INSTANCE,
            null
        );
        #else
        var lines = tooltipLines.stream()
            .map(MoulConfigPlatform::unwrap)
            .map(Language.getInstance()::getVisualOrder)
            .toList();
        drawContext.renderTooltip(
            mc.font,
            lines,
            x, y
        );
        #endif
    }

    #if MC217
    @Value
    static class DrawAction {
        Consumer<RenderContext> action;
        @Nullable
        ScreenRectangle scissorTop;
        Matrix3x2f transform;
    }

    NavigableMap<Layer, List<DrawAction>> queuedLayers = new TreeMap<>();

    @Override
    public void renderExtraLayers() {
        var currentLayer = Layer.ROOT;
        while (true) {
            var nextLayer = queuedLayers.ceilingEntry(currentLayer.next());
            if (nextLayer == null)
                break;
            currentLayer = nextLayer.getKey();

            var draws = nextLayer.getValue();
            for (var draw : draws) {
                if (!drawContext.scissorStack.stack.isEmpty()) {
                    Warnings.warn("Scissors found despite no scissor assertion", 4);
                }

                if (draw.scissorTop != null) {
                    pushRawScissor(draw.scissorTop.left(), draw.scissorTop.top(), draw.scissorTop.right(), draw.scissorTop.bottom());
                }

                pushMatrix();
                getMatrices().set(draw.transform);

                draw.action.accept(this);

                popMatrix();

                if (!drawContext.scissorStack.stack.isEmpty()) {
                    Warnings.warn("Scissors found despite no scissor assertion after execution of ${draw.action}", 4);
                }
            }
        }
    }
    #else

    @Override
    public void renderExtraLayers() {
        // Left blank: [drawOnTop] renders directly.
    }
    #endif
}
