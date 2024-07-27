package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.observer.ObservableList;
import kotlin.Pair;
import lombok.Getter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public class ArrayComponent<T> extends GuiComponent {
    public final ObservableList<T> list;

    public final Function<? super T, ? extends GuiComponent> render;
    public final Supplier<Color> oddRows;
    public final Supplier<Color> evenRows;
    public List<GuiComponent> guiElements;
    public IdentityHashMap<T, GuiComponent> cache = new IdentityHashMap<>();

    private int width, height;

    public ArrayComponent(ObservableList<T> list, Function<? super T, ? extends GuiComponent> render) {
        this(list, render, GetSetter.constant(new Color(0, true)), GetSetter.constant(new Color(0, true)));
    }

    public ArrayComponent(ObservableList<T> list, Function<? super T, ? extends GuiComponent> render, Supplier<Color> oddRows, Supplier<Color> evenRows) {
        this.list = list;
        this.render = render;
        this.oddRows = oddRows;
        this.evenRows = evenRows;
        list.setObserver(this::reinitializeChildren);
        reinitializeChildren();
    }

    public void reinitializeChildren() {
        width = 0;
        height = 0;
        guiElements = new ArrayList<>();
        for (T t : list) {
            GuiComponent apply = cache.computeIfAbsent(t, render);
            apply.foldRecursive((Void) null, ((guiComponent, unused) -> {
                guiComponent.setContext(getContext());
                return null;
            }));
            apply.setContext(getContext());
            width = Math.max(apply.getWidth(), width);
            height += apply.getHeight();
            guiElements.add(apply);
        }
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        for (GuiComponent guiElement : guiElements) {
            initial = visitor.apply(guiElement, initial);
        }
        return initial;
    }

    public void foldWithContext(GuiImmediateContext context, ContextVisitor visitor) {
        foldChildren(new Pair<>(0, 0), (child, position) -> {
            visitor.onContext(child, context.translated(0, position.getFirst(), child.getWidth(), child.getHeight()), position.getSecond());
            return new Pair<>(child.getHeight() + position.getFirst(), position.getSecond() + 1);
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
        context.getRenderContext().pushMatrix();
        foldWithContext(context, (child, childContext, index) -> {
            Color color = (index % 2 == 0 ? evenRows : oddRows).get();
            childContext.getRenderContext().drawColoredRect(0f, 0f, childContext.getWidth(), child.getHeight(), color.getRGB());
            child.render(childContext);
            context.getRenderContext().translate(0, child.getHeight(), 0);
        });
        context.getRenderContext().popMatrix();
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        // TODO: early return
        boolean[] wasHandled = new boolean[1];
        foldWithContext(context, (guiComponent, guiImmediateContext, index) -> {
            if (guiComponent.mouseEvent(mouseEvent, guiImmediateContext)) {
                wasHandled[0] = true;
            }
        });
        return wasHandled[0];
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent keyboardEvent, GuiImmediateContext context) {
        // TODO: early return
        boolean[] wasHandled = new boolean[1];
        foldWithContext(context, (guiComponent, guiImmediateContext, index) -> {
            if (!wasHandled[0] && guiComponent.keyboardEvent(keyboardEvent, guiImmediateContext)) {
                wasHandled[0] = true;
            }
        });
        return wasHandled[0];
    }

    @FunctionalInterface
    public interface ContextVisitor {
        void onContext(GuiComponent child, GuiImmediateContext context, int childIndex);
    }
}
