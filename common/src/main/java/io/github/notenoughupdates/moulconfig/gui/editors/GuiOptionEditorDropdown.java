package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GuiOptionEditorDropdown extends ComponentEditor {
    private final String[] values;
    private final boolean useOrdinal;
    private boolean open = false;
    private Enum<?>[] constants;
    GuiComponent component;

    public GuiOptionEditorDropdown(
        ProcessedOption option,
        String[] values
    ) {
        super(option);
        Class<?> clazz = (Class<?>) option.getType();
        if (Enum.class.isAssignableFrom(clazz)) {
            constants = (Enum<?>[]) (clazz).getEnumConstants();
            this.values = new String[constants.length];
            for (int i = 0; i < constants.length; i++) {
                this.values[i] = constants[i].toString();
            }
            assert values.length > 0;
        } else {
            this.values = values;
            assert values.length > 0;
        }
        this.useOrdinal = clazz == int.class || clazz == Integer.class;
        final String[] finalValues = this.values;
        component = wrapComponent(new GuiComponent() {

            @Override
            public int getWidth() {
                return 280;
            }

            @Override
            public int getHeight() {
                return 25;
            }

            @Override
            public void render(@NotNull GuiImmediateContext context) {
                RenderContext renderContext = context.getRenderContext();
                {
                    int height = getHeight();

                    IFontRenderer fr = IMinecraft.instance.getDefaultFontRenderer();
                    int dropdownWidth = Math.min(context.getWidth() / 3 - 10, 80);
                    int left = getWidth() / 6 - dropdownWidth / 2 + 90;
                    int top = height - 7 - 14;
                    int selected = getSelectedIndex();
                    String selectedString = " - Select - ";
                    if (selected >= 0 && selected < finalValues.length) {
                        selectedString = finalValues[selected];
                    }
                    renderContext.drawDarkRect(left, top, dropdownWidth, 14, false);
                    renderContext.drawString(fr, "\u25BC",
                        left + dropdownWidth - 10,
                        height - 7 - 15 + 5,
                        0xffa0a0a0,
                        false
                    );
                    renderContext.drawStringScaledMaxWidth(selectedString,
                        fr,
                        left + 3,
                        top + 3,
                        false,
                        dropdownWidth - 16,
                        0xffa0a0a0
                    );
                }
                if (open) {
                    int selected = getSelectedIndex();
                    String selectedString = " - Select - ";
                    if (selected >= 0 && selected < finalValues.length) {
                        selectedString = finalValues[selected];
                    }

                    int height = getHeight();

                    IFontRenderer fr = IMinecraft.instance.getDefaultFontRenderer();
                    int dropdownWidth = Math.min(context.getWidth() / 3 - 10, 80);
                    int left = getWidth() / 6 - dropdownWidth / 2 + 90;
                    int top = height - 7 - 14;

                    int dropdownHeight = 13 + 12 * finalValues.length;

                    int main = 0xff202026;
                    int outlineColour = 0xffffffff;

                    renderContext.pushMatrix();
                    renderContext.translate(0, 0, 100);
                    renderContext.drawColoredRect(left, top, left + 1, top + dropdownHeight, outlineColour); //Left
                    renderContext.drawColoredRect(left + 1, top, left + dropdownWidth, top + 1, outlineColour); //Top
                    renderContext.drawColoredRect(left + dropdownWidth - 1, top + 1, left + dropdownWidth, top + dropdownHeight, outlineColour); //Right
                    renderContext.drawColoredRect(left + 1, top + dropdownHeight - 1, left + dropdownWidth - 1, top + dropdownHeight, outlineColour); //Bottom
                    renderContext.drawColoredRect(left + 1, top + 1, left + dropdownWidth - 1, top + dropdownHeight - 1, main); //Middle

                    renderContext.drawColoredRect(left + 1, top + 14 - 1, left + dropdownWidth - 1, top + 14, outlineColour); //Bar
                    int dropdownY = 13;
                    for (String option : finalValues) {
                        if (option.isEmpty()) {
                            option = "<NONE>";
                        }
                        renderContext.drawStringScaledMaxWidth(
                            option,
                            fr,
                            left + 3,
                            top + 3 + dropdownY,
                            false,
                            dropdownWidth - 6,
                            0xffa0a0a0
                        );
                        dropdownY += 12;
                    }

                    //todo fix this rendering under the dropdown
                    /*renderContext.drawStringCenteredScaledMaxWidth(
                        "\u25B2",
                        fr,
                        left + dropdownWidth - 10,
                        height - 7 - 15,
                        false,
                        2,
                        0xffa0a0a0
                    );*/

                    renderContext.drawStringScaledMaxWidth(
                        selectedString,
                        fr,
                        left + 3,
                        top + 3,
                        false,
                        dropdownWidth - 16,
                        0xffa0a0a0
                    );

                    renderContext.popMatrix();
                }
            }

            @Override
            public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
                if (mouseEvent instanceof MouseEvent.Scroll) {
                    open = false;
                    return false;
                }
                if (mouseEvent instanceof MouseEvent.Click) {
                    val click = (MouseEvent.Click) mouseEvent;
                    if (!click.getMouseState() || click.getMouseButton() != 0) return false;
                    int left = 95;
                    int top = 4;
                    int mouseX = context.getMouseX();
                    int mouseY = context.getMouseY();

                    boolean inBounds = mouseX >= left && mouseX <= left + 80 &&
                        mouseY >= top && mouseY <= top + 14;

                    if (inBounds) {
                        open = !open;
                        return true;
                    }
                    if (open) {
                        if (!inBounds) {
                            open = false;
                            if (mouseX >= left && mouseX <= left + 80) {
                                int dropdownY = 13;
                                for (int ordinal = 0; ordinal < finalValues.length; ordinal++) {
                                    if (mouseY >= top + 3 + dropdownY && mouseY <= top + 3 + dropdownY + 12) {
                                        if (constants != null) {
                                            option.set(constants[ordinal]);
                                        } else if (useOrdinal) {
                                            option.set(ordinal);
                                        } else {
                                            option.set(finalValues[ordinal]);
                                        }
                                        return true;
                                    }
                                    dropdownY += 12;
                                }
                            }
                            return true;
                        }
                    }

                }
                return super.mouseEvent(mouseEvent, context);
            }
        });
    }

    @Override
    public @NotNull GuiComponent getDelegate() {
        return component;
    }

    private int getSelectedIndex() {
        Object selectedObject = option.get();
        if (selectedObject == null) return -1;
        if (useOrdinal) {
            return (int) selectedObject;
        } else if (constants != null) {
            return ((Enum) selectedObject).ordinal();
        } else {
            return Arrays.asList(values).indexOf(selectedObject);
        }
    }
}
