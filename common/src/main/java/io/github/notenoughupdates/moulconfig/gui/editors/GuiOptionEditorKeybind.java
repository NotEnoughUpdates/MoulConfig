package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.KeyboardConstants;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
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
    private static final int BUTTON_MIN_WIDTH = 48;
    private static final int BUTTON_HEIGHT = 16;
    private static final int LABEL_PADDING = 12;
    private static final int EDIT_DECORATION_WIDTH = 8;
    private static final int RESET_GAP = 3;
    private static final int RESET_WIDTH = 10;
    private static final int RESET_HEIGHT = 11;
    private static final int RESET_Y_OFFSET = 3;
    private boolean editingKeycode = false;
    GuiComponent component;

    public GuiOptionEditorKeybind(ProcessedOption option, int defaultKeyCode) {
        super(option);
        if (option.getType() != int.class && option.getType() != Integer.class)
            Warnings.warn(ConfigEditorKeybind.class + " can only be applied to int properties.");

        component = wrapComponent(new GuiComponent() {
            private int buttonWidth() {
                int labelWidth = IMinecraft.INSTANCE.getDefaultFontRenderer()
                    .getStringWidth(IMinecraft.INSTANCE.getKeyName((int) option.get()));
                return Math.max(BUTTON_MIN_WIDTH, labelWidth + LABEL_PADDING + EDIT_DECORATION_WIDTH);
            }

            private int buttonY(int contextHeight) {
                return (contextHeight - BUTTON_HEIGHT) / 2;
            }

            @Override
            public int getWidth() {
                return buttonWidth() + RESET_GAP + RESET_WIDTH;
            }

            @Override
            public int getHeight() {
                return 30;
            }

            @Override
            public void render(@NotNull GuiImmediateContext context) {
                int height = context.getHeight();
                RenderContext renderContext = context.getRenderContext();
                int buttonWidth = buttonWidth();
                int buttonY = buttonY(height);

                renderContext.drawTexturedRect(GuiTextures.BUTTON, 0, buttonY, buttonWidth, BUTTON_HEIGHT);


                StructuredText keyName = IMinecraft.INSTANCE.getKeyName((int) option.get());
                StructuredText text = editingKeycode ? StructuredText.of("> ").append(keyName).append(" <") : keyName;
                renderContext.drawStringCenteredScaledMaxWidth(text,
                    IMinecraft.INSTANCE.getDefaultFontRenderer(),
                    buttonWidth / 2f, buttonY + BUTTON_HEIGHT / 2f,
                    false, buttonWidth - 4, 0xFF303030
                );

                int resetX = buttonWidth + RESET_GAP;
                int resetY = buttonY + RESET_Y_OFFSET;

                renderContext.drawTexturedRect(GuiTextures.RESET, resetX, resetY, RESET_WIDTH, RESET_HEIGHT);
                int mouseX = context.getMouseX();
                int mouseY = context.getMouseY();
                if (mouseX >= resetX && mouseX < resetX + RESET_WIDTH &&
                    mouseY >= resetY && mouseY < resetY + RESET_HEIGHT) {
                    renderContext.scheduleDrawTooltip(
                        context.getMouseX(), context.getMouseY(),
                        Collections.singletonList(StructuredText.of("Reset to Default").red()));
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

                if (click.getMouseState() && click.getMouseButton() == IMinecraft.INSTANCE.getMouseConstants().left()) {
                    int buttonWidth = buttonWidth();
                    int buttonY = buttonY(context.getHeight());
                    int mouseX = context.getMouseX();
                    int mouseY = context.getMouseY();
                    if (mouseX >= 0 && mouseX < buttonWidth &&
                        mouseY >= buttonY && mouseY < buttonY + BUTTON_HEIGHT) {
                        editingKeycode = true;
                        return true;
                    }
                    int resetX = buttonWidth + RESET_GAP;
                    int resetY = buttonY + RESET_Y_OFFSET;
                    if (mouseX >= resetX && mouseX < resetX + RESET_WIDTH &&
                        mouseY >= resetY && mouseY < resetY + RESET_HEIGHT) {
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
