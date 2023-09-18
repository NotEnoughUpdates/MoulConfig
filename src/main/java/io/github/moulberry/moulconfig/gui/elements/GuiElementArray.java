package io.github.moulberry.moulconfig.gui.elements;

import io.github.moulberry.moulconfig.gui.GuiContext;
import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.observer.ObservableList;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Getter
public class GuiElementArray<T> extends GuiElementNew {
    public final ObservableList<T> list;

    public final Function<? super T, ? extends GuiElementNew> render;
    public List<GuiElementNew> guiElements;
    public IdentityHashMap<T, GuiElementNew> cache = new IdentityHashMap<>();

    private int width, height;

    public GuiElementArray(ObservableList<T> list, Function<? super T, ? extends GuiElementNew> render) {
        this.list = list;
        this.render = render;
        list.setObserver(this::reinitializeChildren);
        reinitializeChildren();
    }

    @Override
    public void setContext(GuiContext context) {
        super.setContext(context);
        for (GuiElementNew guiElement : guiElements) {
            guiElement.setContext(context);
        }
    }

    public void reinitializeChildren() {
        width = 0;
        height = 0;
        guiElements = new ArrayList<>();
        for (T t : list) {
            GuiElementNew apply = cache.computeIfAbsent(t, render);
            apply.setContext(getContext());
            width = Math.max(apply.getWidth(), width);
            height += apply.getHeight();
            guiElements.add(apply);
        }
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiElementNew, T, T> visitor) {
        for (GuiElementNew guiElement : guiElements) {
            initial = visitor.apply(guiElement, initial);
        }
        return initial;
    }

    public void foldWithContext(GuiImmediateContext context, BiConsumer<GuiElementNew, GuiImmediateContext> visitor) {
        foldChildren(0, (child, position) -> {
            visitor.accept(child, context.translated(0, position, child.getWidth(), child.getHeight()));
            return child.getHeight() + position;
        });
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void render(GuiImmediateContext context) {
        GlStateManager.pushMatrix();
        foldWithContext(context, (child, childContext) -> {
            child.render(childContext);
            GlStateManager.translate(0, child.getHeight(), 0);
        });
        GlStateManager.popMatrix();
    }

    @Override
    public void mouseEvent(GuiImmediateContext context) {
        foldWithContext(context, GuiElementNew::mouseEvent);
    }

    @Override
    public void keyboardEvent(GuiImmediateContext context) {
        foldWithContext(context, GuiElementNew::keyboardEvent);
    }
}
