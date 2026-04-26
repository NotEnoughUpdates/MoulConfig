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

package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.gui.component.*;
import io.github.notenoughupdates.moulconfig.internal.*;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import kotlin.Pair;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public class GuiOptionEditorDraggableList extends ComponentEditor {
    private static final int DROPDOWN_WIDTH = 100;
    private static final int DROPDOWN_ITEM_HEIGHT = 12;
    private static final int DROPDOWN_SCREEN_MARGIN = 4;

    private Map<Object, StructuredText> exampleText = new HashMap<>();
    private boolean enableDeleting;
    private List<Object> activeText;
    private final boolean requireNonEmpty;
    private int dragStartIndex = -1;

    private LerpingInteger2 trashAnimation = new LerpingInteger2(255, 3, 2);
    private Pair<Integer, Integer> lastListRenderPos = new Pair<>(0, 0);

    private Enum<?>[] enumConstants;
    private String exampleTextConcat;
    // TODO: rework this entire thing to accept StructuredTexts and/or classes implementing a custom interfaces and/or a custom text mapper

    public GuiOptionEditorDraggableList(
        ProcessedOption option,
        String[] exampleText,
        boolean enableDeleting
    ) {
        this(option, exampleText, enableDeleting, false);
    }

    public GuiOptionEditorDraggableList(
        ProcessedOption option,
        String[] exampleText,
        boolean enableDeleting,
        boolean requireNonEmpty
    ) {
        super(option);

        this.enableDeleting = enableDeleting;
        this.activeText = (List) option.get();
        this.requireNonEmpty = requireNonEmpty;

        Class<?> elementType = TypeUtils.resolveRawType(((ParameterizedType) option.getType()).getActualTypeArguments()[0]);

        if (Enum.class.isAssignableFrom(elementType)) {
            Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) elementType;
            enumConstants = enumType.getEnumConstants();
            for (int i = 0; i < enumConstants.length; i++) { // TODO: all of this caching is useless, tbh.
                this.exampleText.put(enumConstants[i], StructuredText.of(enumConstants[i].toString()));
            }
        } else {
            for (int i = 0; i < exampleText.length; i++) {
                this.exampleText.put(i, StructuredText.of(exampleText[i]));
            }
        }
    }

    private void saveChanges() {
        option.explicitNotifyChange();
    }

    private StructuredText getExampleText(Object forObject) {
        StructuredText str = exampleText.get(forObject);
        if (str == null) {
            str = StructuredText.of("<unknown " + forObject + ">");
            Warnings.warnOnce("Could not find draggable list object for " + forObject + " on option " + option.getDebugDeclarationLocation(), forObject, option);
        }
        return str;
    }

    public boolean canDeleteRightNow() {
        return enableDeleting && (activeText.size() > 1 || !requireNonEmpty);
    }

    GuiComponent delegate;

    Rect trashCanBoundingBox;

    @Override
    public @NotNull GuiComponent getDelegate() {
        if (delegate == null)
            delegate = wrapComponent(
                new FixedComponent(
                    new RowComponent(
                        new ButtonComponent(new CenterComponent(new TextComponent(StructuredText.of(" Add "))), 2, () -> {
                            var pos = IMinecraft.INSTANCE.getMousePosition();
                            if (activeText.size() == exampleText.size())
                                return;
                            openDropDownOverlay(pos.getFirst(), pos.getSecond());
                        }),
                        new SpacerComponent(GetSetter.constant(5), GetSetter.constant(0)),
                        new GuiComponent() {
                            @Override
                            public int getWidth() {
                                return 11;
                            }

                            @Override
                            public int getHeight() {
                                return 14;
                            }

                            @Override
                            public void render(@NotNull GuiImmediateContext context) {
                                if (context.isHovered() && dragStartIndex >= 0 && canDeleteRightNow()) {
                                    trashAnimation.setTarget(0);
                                } else {
                                    trashAnimation.setTarget(255);
                                }
                                int nonRedTints = trashAnimation.getValue();
                                context.getRenderContext().drawComplexTexture(
                                    GuiTextures.DELETE,
                                    0F, 0F, 11F, 14F,
                                    draw -> draw.color(ColourUtil.packARGB(255, 255, nonRedTints, nonRedTints))
                                );
                                trashCanBoundingBox = Rect.ofGuiImmediateContext(context);
                            }
                        }),
                    48, 16),
                new GuiComponent() {
                    @Override
                    public int getWidth() {
                        return 0;
                    }

                    @Override
                    public int getHeight() {
                        int height = 5;
                        var fr = IMinecraft.INSTANCE.getDefaultFontRenderer();
                        for (Object object : activeText) {
                            StructuredText str = getExampleText(object);
                            height += (fr.getHeight() + 1) * fr.splitLines(str).size();
                        }
                        return height;
                    }

                    @Override
                    public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
                        if (mouseEvent instanceof MouseEvent.Click) {
                            var click = (MouseEvent.Click) mouseEvent;
                            var fr = IMinecraft.INSTANCE.getDefaultFontRenderer();
                            if (click.getMouseState()) {
                                int i = 0;
                                int yOff = 0;
                                for (Object indexObject : activeText) {
                                    StructuredText str = getExampleText(indexObject);
                                    var multilines = fr.splitLines(str);
                                    int ySize = multilines.size() * (fr.getHeight() + 1);
                                    var trans = context.translated(0, yOff, context.getWidth(), ySize);
                                    if (trans.isHovered()) {
                                        dragStartIndex = i;
                                        var mouseY = trans.getMouseY() - 4;
                                        openOverlay(makeDragComponent(indexObject, trans.getMouseX(), mouseY, context.getWidth()),
                                            // context.getRenderOffsetX()
                                            context.getAbsoluteMouseX() - trans.getMouseX(),
                                            context.getAbsoluteMouseY() - mouseY);
                                        return true;
                                    }
                                    i++;
                                    yOff += ySize;
                                }
                            }
                        }
                        return super.mouseEvent(mouseEvent, context);
                    }

                    @Override
                    public void render(@NotNull GuiImmediateContext context) {
                        lastListRenderPos = new Pair<>(context.getRenderOffsetX(), context.getRenderOffsetY());
                        var renderContext = context.getRenderContext();
                        var width = context.getWidth();
                        var fr = IMinecraft.INSTANCE.getDefaultFontRenderer();
                        var height = context.getHeight();
                        renderContext.drawColoredRect(0, 0, width, height, 0xffdddddd);
                        renderContext.drawColoredRect(1, 1, width - 1, height - 1, 0xff000000);

                        int i = 0;
                        int yOff = 0;
                        for (Object indexObject : activeText) {
                            StructuredText str = getExampleText(indexObject);

                            var multilines = fr.splitLines(str);

                            int ySize = multilines.size() * (fr.getHeight() + 1);

                            if (i++ != dragStartIndex) {
                                for (int multilineIndex = 0; multilineIndex < multilines.size(); multilineIndex++) {
                                    var line = multilines.get(multilineIndex);
                                    renderContext.drawStringScaledMaxWidth(line, fr,
                                        15, 5 + yOff + multilineIndex * 10, true, width - 20, 0xffffffff
                                    );
                                }
                                renderContext.drawString(
                                    fr,
                                    StructuredText.of("≡"),
                                    5,
                                    4 + yOff + ySize / 2 - 4,
                                    0xffffff,
                                    true
                                );
                            }

                            yOff += ySize;
                        }
                    }
                }
            );
        return delegate;
    }


    GuiComponent makeDragComponent(Object indexObject, int mouseOffsetX, int mouseOffsetY, int width) {
        return new GuiComponent() {
            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return 11;
            }

            @Override
            public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
                if (mouseEvent instanceof MouseEvent.Click) {
                    var click = (MouseEvent.Click) mouseEvent;
                    if (!click.getMouseState()) {
                        closeOverlay();
                        if (canDeleteRightNow() && trashCanBoundingBox.includesPoint(context.getAbsoluteMouseX(), context.getAbsoluteMouseY())) {
                            activeText.remove(dragStartIndex);
                            saveChanges();
                        }
                        dragStartIndex = -1;
                        return true;
                    }
                }
                if (mouseEvent instanceof MouseEvent.Move) {
                    var mx = context.getAbsoluteMouseX() - mouseOffsetX;
                    var my = context.getAbsoluteMouseY() - mouseOffsetY;
                    openOverlay(getOverlayDelegate(), mx, my);
                    reorderElements(width, mx, my);
                }
                return super.mouseEvent(mouseEvent, context);
            }

            @Override
            public void render(@NotNull GuiImmediateContext context) {
                var renderContext = context.getRenderContext();
                var fr = IMinecraft.INSTANCE.getDefaultFontRenderer();
                var text = getExampleText(indexObject);
                var firstLine = fr.splitLines(text).get(0);
                renderContext.drawString(
                    fr,
                    StructuredText.of("≡"),
                    5,
                    1,
                    0xffffff,
                    true
                );
                renderContext.drawStringScaledMaxWidth(firstLine, fr,
                    15, 1, true, context.getWidth() - 20, 0xffffffff
                );
                // TODO: make this transparent via texty things
            }
        };
    }

    private void reorderElements(int width, int mouseX, int mouseY) {
        assert lastListRenderPos != null;
        int renderX = lastListRenderPos.getFirst();
        if (mouseX < renderX || mouseX > renderX + width)
            return;
        int renderY = lastListRenderPos.getSecond();
        var fr = IMinecraft.INSTANCE.getDefaultFontRenderer();
        int i = 0;
        int yOff = renderY;
        for (Object indexObject : activeText) {
            StructuredText str = getExampleText(indexObject);

            var multilines = fr.splitLines(str);

            int ySize = multilines.size() * (fr.getHeight() + 1);
            if (yOff > mouseY && mouseY < yOff + ySize) {
                var toSwap = activeText.get(i);
                var moving = activeText.get(dragStartIndex);
                activeText.set(i, moving);
                activeText.set(dragStartIndex, toSwap);
                // TODO: technically you arent supposed to swap here, instead move all the in between elements over by one
                //       in practice this is fine as long as you dont take the element the long way around.
                dragStartIndex = i;
                return;
            }

            i++;
            yOff += ySize;
        }
        saveChanges();
    }

    private List<Object> getRemainingDropDownEntries() {
        List<Object> remaining = new ArrayList<>(exampleText.keySet());
        remaining.removeAll(activeText);
        return remaining;
    }

    private int getDropDownContentHeight() {
        return Math.max(0, -1 + DROPDOWN_ITEM_HEIGHT * getRemainingDropDownEntries().size());
    }

    private int getDropDownVisibleHeight(int overlayY) {
        int screenHeight = IMinecraft.INSTANCE.getScaledHeight();
        int maxHeight = Math.max(DROPDOWN_ITEM_HEIGHT, screenHeight - overlayY - DROPDOWN_SCREEN_MARGIN);
        return Math.min(getDropDownContentHeight(), maxHeight);
    }

    private void openDropDownOverlay(int mouseX, int mouseY) {
        int screenHeight = IMinecraft.INSTANCE.getScaledHeight();
        int screenWidth = IMinecraft.INSTANCE.getScaledWidth();
        int contentHeight = getDropDownContentHeight();
        int maxVisibleHeight = Math.max(DROPDOWN_ITEM_HEIGHT, screenHeight - DROPDOWN_SCREEN_MARGIN * 2);
        int visibleHeight = Math.min(contentHeight, maxVisibleHeight);
        int overlayX = Math.min(mouseX, screenWidth - DROPDOWN_WIDTH - DROPDOWN_SCREEN_MARGIN);
        int overlayY = Math.min(mouseY, screenHeight - visibleHeight - DROPDOWN_SCREEN_MARGIN);
        overlayX = Math.max(DROPDOWN_SCREEN_MARGIN, overlayX);
        overlayY = Math.max(DROPDOWN_SCREEN_MARGIN, overlayY);
        openOverlay(makeDropDownOverlay(overlayY), overlayX, overlayY);
    }

    GuiComponent makeDropDownOverlay(int overlayY) {
        return new GuiComponent() {
            int scrollOffset;

            @Override
            public int getWidth() {
                return DROPDOWN_WIDTH; // TODO: dynamically decide on a size
            }

            @Override
            public int getHeight() {
                return getDropDownVisibleHeight(overlayY);
            }

            @Override
            public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
                int maxScrollOffset = Math.max(0, getDropDownContentHeight() - context.getHeight());
                if (scrollOffset > maxScrollOffset) {
                    scrollOffset = maxScrollOffset;
                }
                if (context.isHovered() && mouseEvent instanceof MouseEvent.Scroll) {
                    scrollOffset = (int) Math.max(0, Math.min(
                        scrollOffset - (((MouseEvent.Scroll) mouseEvent).getDWheel() * 15),
                        maxScrollOffset
                    ));
                    return true;
                }
                if (mouseEvent instanceof MouseEvent.Click) {
                    var click = (MouseEvent.Click) mouseEvent;
                    if (click.getMouseState() && context.isHovered()) {
                        List<Object> remaining = getRemainingDropDownEntries();
                        int dropdownY = -1;
                        for (Object indexObject : remaining) {
                            if (context.translated(0, dropdownY + 3 - scrollOffset, context.getWidth(), 10).isHovered()) {
                                activeText.add(indexObject);
                                return true;
                            }
                            dropdownY += DROPDOWN_ITEM_HEIGHT;
                        }
                    } else if (click.getMouseState()) {
                        closeOverlay();
                    }
                }
                return super.mouseEvent(mouseEvent, context);
            }

            @Override
            public void render(@NotNull GuiImmediateContext context) {
                List<Object> remaining = getRemainingDropDownEntries();
                if (remaining.isEmpty()) {
                    closeOverlay();
                    return;
                }

                int maxScrollOffset = Math.max(0, getDropDownContentHeight() - context.getHeight());
                if (scrollOffset > maxScrollOffset) {
                    scrollOffset = maxScrollOffset;
                }

                int dropdownHeight = context.getHeight();
                int dropdownWidth = context.getWidth();
                var fr = IMinecraft.INSTANCE.getDefaultFontRenderer();
                var renderContext = context.getRenderContext();
                int main = 0xff202026;
                int outline = 0xff404046;
                renderContext.drawColoredRect(0, 0, 1, dropdownHeight, outline); //0
                renderContext.drawColoredRect(1, 0, dropdownWidth, 1, outline); //0
                renderContext.drawColoredRect(dropdownWidth - 1, 1, dropdownWidth, dropdownHeight, outline); //Right
                renderContext.drawColoredRect(
                    1,
                    dropdownHeight - 1,
                    dropdownWidth - 1,
                    dropdownHeight,
                    outline
                ); //Bottom
                renderContext.drawColoredRect(1, 1, dropdownWidth - 1, dropdownHeight - 1, main); //Middle

                renderContext.pushRawScissor(
                    context.getRenderOffsetX() + 1,
                    context.getRenderOffsetY() + 1,
                    context.getRenderOffsetX() + dropdownWidth - 1,
                    context.getRenderOffsetY() + dropdownHeight - 1
                );
                renderContext.pushMatrix();
                renderContext.translate(0, -scrollOffset);
                int dropdownY = -1;
                for (Object indexObject : remaining) {
                    StructuredText str = getExampleText(indexObject);
                    if (str.getText().isEmpty()) {
                        str = StructuredText.of("<NONE>");
                    }
                    renderContext.drawStringScaledMaxWidth(fr.splitLines(str).get(0),
                        fr, 3, 3 + dropdownY, false, dropdownWidth - 6, 0xffa0a0a0
                    );
                    dropdownY += DROPDOWN_ITEM_HEIGHT;
                }
                renderContext.popMatrix();
                renderContext.popScissor();
            }
        };
    }


    @Override
    public boolean fulfillsSearch(String word) {
        if (exampleTextConcat == null) {
            exampleTextConcat = exampleText.values().stream().map(StructuredText::getText).collect(Collectors.joining(" "))
                .toLowerCase(Locale.ROOT);
        }
        return super.fulfillsSearch(word) || exampleTextConcat.contains(word);
    }
}
