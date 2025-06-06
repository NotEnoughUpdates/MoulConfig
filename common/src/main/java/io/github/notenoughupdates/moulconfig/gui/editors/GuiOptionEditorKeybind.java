package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.KeyboardConstants;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class GuiOptionEditorKeybind extends ComponentEditor {
    private boolean editingKeycode = false;
    GuiComponent component;

    public GuiOptionEditorKeybind(ProcessedOption option, int defaultKeyCode) {
        super(option);
        if (option.getType() != int.class && option.getType() != Integer.class)
            Warnings.warn(ConfigEditorKeybind.class + " can only be applied to int properties.");

        component = wrapComponent(new GuiComponent() {
            @Override
            public int getWidth() {
                return 0;
            }

            @Override
            public int getHeight() {
                return 30;
            }

            @Override
            public void render(@NotNull GuiImmediateContext context) {
                int height = getHeight();
                RenderContext renderContext = context.getRenderContext();
                int width = getWidth();

                renderContext.color(1, 1, 1, 1);
                renderContext.bindTexture(GuiTextures.BUTTON);
                renderContext.drawTexturedRect(width / 6 - 24, height - 7 - 14, 48, 16);


                String keyName = IMinecraft.instance.getKeyName((int) option.get());
                String text = editingKeycode ? "> " + keyName + " <" : keyName;
                renderContext.drawStringCenteredScaledMaxWidth(text,
                                                               IMinecraft.instance.getDefaultFontRenderer(),
                                                               width / 6, height - 7 - 6,
                                                               false, 38, 0xFF303030
                );

                int resetX = width / 6 - 24 + 48 + 3;
                int resetY = height - 7 - 14 + 3;

                renderContext.bindTexture(GuiTextures.RESET);
                renderContext.color(1, 1, 1, 1);
                renderContext.drawTexturedRect(resetX, resetY, 10, 11);
                int mouseX = context.getMouseX();
                int mouseY = context.getMouseY();
                if (mouseX >= resetX && mouseX < resetX + 10 &&
                    mouseY >= resetY && mouseY < resetY + 11) {
                    renderContext.scheduleDrawTooltip(Collections.singletonList(
                        "§cReset to Default"
                    ));
                }
            }

            @Override
            public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
                if (!(mouseEvent instanceof MouseEvent.Click)) return false;
                MouseEvent.Click click = (MouseEvent.Click) mouseEvent;
                if (click.getMouseState() && click.getMouseButton() != -1 && editingKeycode) {
                    editingKeycode = false;
                    int mouseButton = click.getMouseButton();
                    option.set(mouseButton); // TODO: make this distinct. This is also different from the way 1.8.9 handles those keybindings, so this class is incompatible right now. A "proper" way to do this would be to make a Keybinding class that stores both the button and whether this is a mouse or keyboard button, with some version specific helpers to test if an event matches.
                    return true;
                }

                if (click.getMouseState() && click.getMouseButton() == 0) {
                    int height = getHeight();
                    int width = getHeight();
                    int mouseX = context.getMouseX();
                    int mouseY = context.getMouseY();
                    if (mouseX > width / 6 - 24 && mouseX < width / 6 + 16 &&
                        mouseY > height - 7 - 14 && mouseY < height - 7 + 2) {
                        editingKeycode = true;
                        return true;
                    }
                    if (mouseX > width / 6 - 24 + 48 - 3 && mouseX < width / 6 - 24 + 48 + 13 - 5 &&
                        mouseY > height - 7 - 14 + 3 && mouseY < height - 7 - 14 + 3 + 11) {
                        option.set(defaultKeyCode);
                        return true;
                    }
                }

                return false;
            }

            @Override
            public boolean keyboardEvent(@NotNull KeyboardEvent keyboardEvent, @NotNull GuiImmediateContext context) {
                if (keyboardEvent instanceof KeyboardEvent.KeyPressed) {
                    var keyPressed = (KeyboardEvent.KeyPressed) keyboardEvent;
                    if (editingKeycode) {
                        if (keyPressed.getPressed()) return true;
                        editingKeycode = false;
                        int keycode = keyPressed.getKeycode();
                        if (keycode == KeyboardConstants.INSTANCE.getEscape() || keycode == 0) {
                            keycode = KeyboardConstants.INSTANCE.getNone();
                        }
                        option.set(keycode);
                        return true;
                    } else {
                        return false;
                    }
                }

                return editingKeycode;
            }
        });
    }

    @Override
    public @NotNull GuiComponent getDelegate() {
        return component;
    }
}
