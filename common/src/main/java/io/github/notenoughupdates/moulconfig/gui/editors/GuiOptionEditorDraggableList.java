package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.internal.LerpUtils;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class GuiOptionEditorDraggableList extends ComponentEditor {

    GuiComponent component;
    private Map<Object, String> exampleText = new HashMap<>();
    private boolean enableDeleting;
    private List<Object> activeText;
    private final boolean requireNonEmpty;
    private Object currentDragging = null;
    private int dragStartIndex = -1;

    private long trashHoverTime = -1;

    private int dragOffsetX = -1;
    private int dragOffsetY = -1;
    private boolean dropdownOpen = false;
    private Enum<?>[] enumConstants;
    private String exampleTextConcat;

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

        Type elementType = ((ParameterizedType) option.getType()).getActualTypeArguments()[0];

        if (/*Enum.class.isAssignableFrom((Class<?>) elementType)*/ false) {
            //todo i got a class cast exception here
           /* Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) ((ParameterizedType) option.getType()).getActualTypeArguments()[0];
            enumConstants = enumType.getEnumConstants();
            for (int i = 0; i < enumConstants.length; i++) {
                this.exampleText.put(enumConstants[i], enumConstants[i].toString());
            }*/
        } else {
            for (int i = 0; i < exampleText.length; i++) {
                this.exampleText.put(i, exampleText[i]);
            }
        }

        Map<Object, String> finalExampleText = this.exampleText;
        component = wrapComponent(new GuiComponent() {

            @Override
            public int getWidth() {
                return 300;
            }

            @Override
            public int getHeight() {
                int height = 40 + 17;

                for (Object object : activeText) {
                    String str = getExampleText(object);
                    height += 10 * str.split("\n").length;
                }

                return height;
            }

            @Override
            public void render(@NotNull GuiImmediateContext context) {
                int height = getHeight();
                int width = getWidth();

                RenderContext renderContext = context.getRenderContext();
                renderContext.translate(100,0, 0);
                renderContext.translate(0,-10, 0);
                renderContext.color(1, 1, 1, 1);
                IMinecraft.instance.bindTexture(GuiTextures.BUTTON);
                renderContext.drawTexturedRect(width / 6 - 24, 45 - 7 - 14, 48, 16);

                IFontRenderer fr = IMinecraft.instance.getDefaultFontRenderer();
                renderContext.drawStringCenteredScaledMaxWidth("Add", fr,
                    width / 6, 45 - 7 - 6,
                    false, 44, 0xFF303030
                );

                long currentTime = System.currentTimeMillis();
                if (trashHoverTime < 0) {
                    float greenBlue = LerpUtils.clampZeroOne((currentTime + trashHoverTime) / 250f);
                    renderContext.color(1, greenBlue, greenBlue, 1);
                } else {
                    float greenBlue = LerpUtils.clampZeroOne((250 + trashHoverTime - currentTime) / 250f);
                    renderContext.color(1, greenBlue, greenBlue, 1);
                }

                if (canDeleteRightNow()) {
                    int deleteX = width / 6 + 27;
                    int deleteY = 45 - 7 - 13;
                    renderContext.bindTexture(GuiTextures.DELETE);
                    renderContext.drawTexturedRect(deleteX, deleteY, 11, 14);

                    int mouseX = context.getMouseX();
                    int mouseY = context.getMouseY();
                    if (currentDragging == null &&
                        mouseX >= deleteX && mouseX < deleteX + 11 &&
                        mouseY >= deleteY && mouseY < deleteY + 14) {
                        renderContext.scheduleDrawTooltip(Collections.singletonList(
                            "§cDelete Item"
                        ));
                    }
                }

                renderContext.drawColoredRect(5, 45, width - 5, height - 5, 0xffdddddd);
                renderContext.drawColoredRect(6, 46, width - 6, height - 6, 0xff000000);

                int i = 0;
                int yOff = 0;
                for (Object indexObject : activeText) {
                    String str = getExampleText(indexObject);

                    String[] multilines = str.split("\n");

                    int ySize = multilines.length * 10;

                    if (i++ != dragStartIndex) {
                        for (int multilineIndex = 0; multilineIndex < multilines.length; multilineIndex++) {
                            String line = multilines[multilineIndex];
                            renderContext.drawStringScaledMaxWidth(line + "§r", fr,
                                20, 50 + yOff + multilineIndex * 10, true, width - 20, 0xffffffff
                            );
                        }
                        renderContext.drawString(
                            fr,
                            "≡",
                            10,
                            49 + yOff + ySize / 2 - 4,
                            0xffffff,
                            true
                        );
                    }

                    yOff += ySize;
                }

                if (dropdownOpen) {
                    List<Object> remaining = new ArrayList<>(finalExampleText.keySet());
                    remaining.removeAll(activeText);

                    int dropdownWidth = Math.min(width / 2 - 10, 150);
                    int left = dragOffsetX;
                    int top = dragOffsetY;

                    int dropdownHeight = -1 + 12 * remaining.size();

                    int main = 0xff202026;
                    int outline = 0xff404046;
                    renderContext.drawColoredRect(left, top, left + 1, top + dropdownHeight, outline); //Left
                    renderContext.drawColoredRect(left + 1, top, left + dropdownWidth, top + 1, outline); //Top
                    renderContext.drawColoredRect(left + dropdownWidth - 1, top + 1, left + dropdownWidth, top + dropdownHeight, outline); //Right
                    renderContext.drawColoredRect(
                        left + 1,
                        top + dropdownHeight - 1,
                        left + dropdownWidth - 1,
                        top + dropdownHeight,
                        outline
                    ); //Bottom
                    renderContext.drawColoredRect(left + 1, top + 1, left + dropdownWidth - 1, top + dropdownHeight - 1, main); //Middle

                    int dropdownY = -1;
                    for (Object indexObject : remaining) {
                        String str = getExampleText(indexObject);
                        if (str.isEmpty()) {
                            str = "<NONE>";
                        }
                        renderContext.drawStringScaledMaxWidth(str.replaceAll("(\n.*)+", " ..."),
                            fr, left + 3, top + 3 + dropdownY, false, dropdownWidth - 6, 0xffa0a0a0
                        );
                        dropdownY += 12;
                    }
                } else if (currentDragging != null) {
                    int opacity = 0x80;
                    if (trashHoverTime < 0) {
                        float greenBlue = LerpUtils.clampZeroOne((currentTime + trashHoverTime) / 250f);
                        opacity = (int) (opacity * greenBlue);
                    } else {
                        float greenBlue = LerpUtils.clampZeroOne((250 + trashHoverTime - currentTime) / 250f);
                        opacity = (int) (opacity * greenBlue);
                    }

                    if (opacity < 20) return;

                    //todo yes this is very scuffed
                    int mouseX = context.getMouseX() - 90;
                    int mouseY = context.getMouseY() + 10;

                    String str = getExampleText(currentDragging);
                    String[] multilines = str.split("\n");

                    //todo this needs blend
                    //GlStateManager.enableBlend();
                    for (int multilineIndex = 0; multilineIndex < multilines.length; multilineIndex++) {
                        String line = multilines[multilineIndex];
                        renderContext.drawStringScaledMaxWidth(
                            line + "§r",
                            fr,
                            dragOffsetX + mouseX + 10,
                            dragOffsetY + mouseY + multilineIndex * 10,
                            true,
                            width - 20,
                            0xffffff | (opacity << 24)
                        );
                    }

                    int ySize = multilines.length * 10;

                    renderContext.drawString(fr,"\u2261",
                        dragOffsetX + mouseX,
                        dragOffsetY - 1 + mouseY + ySize / 2 - 4, 0xffffff, true
                    );
                }
            }

            boolean isMouseDown = false;
            boolean justClicked = false;
            @Override
            public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
                context = context.translatedNonRendering(0, -10,0,0 );
                if (mouseEvent instanceof MouseEvent.Scroll) {
                    dropdownOpen = false;
                    return false;
                }
                int mouseX = context.getMouseX()- 100;
                int mouseY = context.getMouseY();
                int width = getWidth();
                if (mouseEvent instanceof MouseEvent.Click) {
                    MouseEvent.Click click = (MouseEvent.Click) mouseEvent;
                    System.out.println(click.getMouseButton());
                    justClicked = !isMouseDown;
                    isMouseDown = click.getMouseState();


                    System.out.println(mouseX);
                    System.out.println(mouseX >= width / 6 + 27 - 3);
                    System.out.println(width / 6 + 27 - 3);
                    System.out.println(mouseX <= width / 6 + 27 + 11 + 3);
                    System.out.println(width / 6 + 27 + 11 + 3);
                    if (!click.getMouseState() && !dropdownOpen &&
                        dragStartIndex >= 0 && click.getMouseButton() == 0 &&
                        mouseX >= width / 6 + 27 - 3 && mouseX <= width / 6 + 27 + 11 + 3 &&
                        mouseY >= 45 - 7 - 13 - 3 && mouseY <= 45 - 7 - 13 + 14 + 3) {
                        if (canDeleteRightNow()) {
                            activeText.remove(dragStartIndex);
                            saveChanges();
                        }
                        currentDragging = null;
                        dragStartIndex = -1;
                        return false;
                    }
                } else {
                    if (justClicked) {
                        justClicked = false;
                    }
                }


                if (!isMouseDown || dropdownOpen) {
                    currentDragging = null;
                    dragStartIndex = -1;
                    if (trashHoverTime > 0 && canDeleteRightNow()) trashHoverTime = -System.currentTimeMillis();
                } else if (currentDragging != null &&
                    mouseX >= width / 6 + 27 - 3 && mouseX <= width / 6 + 27 + 11 + 3 &&
                    mouseY >= 45 - 7 - 13 - 3 && mouseY <= 45 - 7 - 13 + 14 + 3) {
                    if (trashHoverTime < 0 && canDeleteRightNow()) trashHoverTime = System.currentTimeMillis();
                } else if (!canDeleteRightNow()){
                    trashHoverTime = Long.MAX_VALUE;
                } else if (trashHoverTime > 0) {
                    trashHoverTime = -System.currentTimeMillis();
                }

                if (isMouseDown) {
                    int height = getHeight();

                    if (dropdownOpen && justClicked) {
                        List<Object> remaining = new ArrayList<>(finalExampleText.keySet());
                        remaining.removeAll(activeText);

                        int dropdownWidth = Math.min(width / 2 - 10, 150);
                        int left = dragOffsetX;
                        int top = dragOffsetY;

                        int dropdownHeight = -1 + 12 * remaining.size();

                        if (mouseX > left && mouseX < left + dropdownWidth &&
                            mouseY > top && mouseY < top + dropdownHeight) {
                            int dropdownY = -1;
                            for (Object objectIndex : remaining) {
                                if (mouseY < top + dropdownY + 12) {
                                    activeText.add(0, objectIndex);
                                    saveChanges();
                                    if (remaining.size() == 1) dropdownOpen = false;
                                    return true;
                                }

                                dropdownY += 12;
                            }
                        }

                        dropdownOpen = false;
                        return true;
                    }

                    if (activeText.size() < finalExampleText.size() &&
                        mouseX > width / 6 - 24 && mouseX < width / 6 + 24 &&
                        mouseY > 45 - 7 - 14 && mouseY < 45 - 7 + 2 && justClicked) {
                        dropdownOpen = true;
                        dragOffsetX = mouseX;
                        dragOffsetY = mouseY;
                        return true;
                    }

                    if (justClicked &&
                        mouseX > 5 && mouseX < width - 5 &&
                        mouseY > 45 && mouseY < height - 6) {
                        int yOff = 0;
                        int i = 0;
                        for (Object objectIndex : activeText) {
                            String str = getExampleText(objectIndex);
                            int ySize = 10 * str.split("\n").length;
                            if (mouseY < 50 + yOff + ySize) {
                                dragOffsetX = 10 - mouseX;
                                dragOffsetY = 50 + yOff - mouseY;

                                currentDragging = objectIndex;
                                dragStartIndex = i;
                                break;
                            }
                            yOff += ySize;
                            i++;
                        }
                    }
                }

                if (!justClicked && currentDragging != null) {
                    int yOff = 0;
                    int i = 0;
                    for (Object objectIndex : activeText) {
                        if (dragOffsetY + mouseY + 4 < 50 + yOff + 10) {
                            activeText.remove(dragStartIndex);
                            activeText.add(i, currentDragging);
                            saveChanges();
                            dragStartIndex = i;
                            break;
                        }
                        String str = getExampleText(objectIndex);
                        yOff += 10 * str.split("\n").length;
                        i++;
                    }
                }


                return false;
            }
        });
    }

    private void saveChanges() {
        option.explicitNotifyChange();
    }

    private String getExampleText(Object forObject) {
        String str = exampleText.get(forObject);
        if (str == null) {
            str = "<unknown " + forObject + ">";
            Warnings.warnOnce("Could not find draggable list object for " + forObject + " on option " + option.getCodeLocation(), forObject, option);
        }
        return str;
    }

    @Override
    public int getHeight() {
        int height = 60;

        for (Object object : activeText) {
            String str = getExampleText(object);
            height += 10 * str.split("\n").length;
        }

        return height;
    }

    public boolean canDeleteRightNow() {
        return enableDeleting && (activeText.size() > 1 || !requireNonEmpty);
    }

    @Override
    public boolean fulfillsSearch(String word) {
        if (exampleTextConcat == null) {
            exampleTextConcat = String.join("", exampleText.values()).toLowerCase(Locale.ROOT);
        }
        return super.fulfillsSearch(word) || exampleTextConcat.contains(word);
    }

    @Override
    public @NotNull GuiComponent getDelegate() {
        return component;
    }
}
