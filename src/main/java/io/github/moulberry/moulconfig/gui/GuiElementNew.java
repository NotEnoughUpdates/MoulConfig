/*
 * Copyright (C) 2023 NotEnoughUpdates contributors
 *
 * This file is part of MoulConfig.
 *
 * MoulConfig is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * MoulConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MoulConfig. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.github.moulberry.moulconfig.gui;
import net.minecraft.client.gui.DrawContext;

import java.util.function.BiFunction;

/**
 * This is the base class for a new GuiElement, meant to remedy some problems of {@link GuiElement}.
 * Most notably, instances of this class are not aware of their position, and instead receive all coordinates passed in
 * (in methods such as mouseClick) as coordinates relative to themselves, and draw onto a pre transformed GL matrix stack.
 * Additionally, these elements now properly handle focus.
 */
public abstract class GuiElementNew {
    GuiContext context;

    protected GuiElementNew() {
    }

    public void setContext(GuiContext context) {
        this.context = context;
    }

    public GuiContext getContext() {
        return context;
    }

    /**
     * Get the requested x size for this element. This is opposed to the actual size given in the {@link GuiImmediateContext context}. Giving this element less space than this may result in misaligned or overlapping rendering.
     */
    public abstract int getWidth();

    /**
     * Get the requested y size for this element. This is opposed to the actual size given in the {@link GuiImmediateContext context}. Giving this element less space than this may result in misaligned or overlapping rendering.
     */
    public abstract int getHeight();

    /**
     * Call this method to request focus in the current gui context.
     */
    public void requestFocus() {
        context.setFocusedElement(this);
    }

    /**
     * Test if this element is focused.
     *
     * @return true if this is focused directly, and has no focused child
     */
    public boolean isFocused() {
        return context.getFocusedElement() == this;
    }

    /**
     * Test if this element is focused, or has a focused child.
     *
     * @return true if this or a child of this element is focused in the current gui context
     */
    public boolean isInFocus() {
        return foldRecursive(false, (element, isFocused) -> isFocused || element.isFocused());
    }

    /**
     * This method is called by the gui context when an element loses focus. This includes a child element losing focus,
     * but not if another child element becomes focused.
     */
    public void onLostFocus() {
    }

    /**
     * Walk the scene tree of this gui element, including children of children.
     * By default, the visitor is called with only this element.
     *
     * @param visitor a consumer to be invoked for all gui elements in the scene tree. the returned value is then passed on to the next invocation.
     * @param initial an initial value to be given to the function
     */
    public final <T> T foldRecursive(T initial, BiFunction<GuiElementNew, T, T> visitor) {
        return foldChildren(visitor.apply(this, initial), (element, state) -> element.foldRecursive(state, visitor));
    }

    /**
     * Walk over the direct children of this element.
     * A gui element that composites other gui elements should override this method.
     * By default, the initial element is returned without change.
     *
     * @param visitor a consumer to be called for all children for this element.
     * @param initial an initial value to be given to the function
     */
    public <T> T foldChildren(T initial, BiFunction<GuiElementNew, T, T> visitor) {
        return initial;
    }

    /**
     * Called by the parent renderer. The GL matrix stack is pre-transformed, so rendering can begin at {@code (0, 0)}
     * and should not render beyond what the context says is available.
     *
     * @param context the context in which this
     */
    public abstract void render(DrawContext drawContext, GuiImmediateContext context);

    /**
     * Called by the parent renderer. Use {@link org.lwjgl.input.Mouse} to obtain information about the event.
     *
     * @param context the context in which this
     */
    public void mouseEvent(int button, GuiImmediateContext context) {
    }

    /**
     * Called by the parent renderer. Use {@link org.lwjgl.input.Keyboard} to obtain information about the event.
     * <p>N.B.: this method is called regardless of whether this element is focused or not.</p>
     *
     * @param context the context in which this
     */
    public void keyboardEvent(GuiImmediateContext context) {
    }

}
