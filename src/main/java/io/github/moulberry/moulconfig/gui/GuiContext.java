package io.github.moulberry.moulconfig.gui;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * A GuiContext manages focus and global state of a collection of {@link GuiElementNew gui elements}.
 */
@Getter
@ToString
@Setter
public class GuiContext {
    /**
     * The root element of this GuiContext
     */
    public final GuiElementNew root;
    public GuiElementNew focusedElement;
    public List<FloatingGuiElement> floatingWindows = new ArrayList<>();

    public GuiContext(GuiElementNew root) {
        this.root = root;
        root.foldRecursive((Void) null, (guiElementNew, _void) -> {
            guiElementNew.setContext(this);
            return _void;
        });
    }
}
