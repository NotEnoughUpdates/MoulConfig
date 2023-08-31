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

package io.github.moulberry.moulconfig.gui.editors;/*
 * Copyright (C) 2022 NotEnoughUpdates contributors
 *
 * This file is part of NotEnoughUpdates.
 *
 * NotEnoughUpdates is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * NotEnoughUpdates is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NotEnoughUpdates. If not, see <https://www.gnu.org/licenses/>.
 */


import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.gui.elements.GuiElementSlider;
import io.github.moulberry.moulconfig.gui.elements.GuiElementTextField;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class GuiOptionEditorSlider extends GuiOptionEditor {
    private final GuiElementSlider slider;
    private final GuiElementTextField textField;

    public GuiOptionEditorSlider(ProcessedOption option, float minValue, float maxValue, float minStep) {
        super(option);
        if (minStep < 0) minStep = 0.01f;

        float floatVal = getFloatValue();
        textField = new GuiElementTextField(
                getStringifiedFloatValue(),
                GuiElementTextField.NO_SPACE | GuiElementTextField.NUM_ONLY | GuiElementTextField.SCALE_TEXT
        );

        slider = new GuiElementSlider(0, 0, 80, minValue, maxValue, minStep, floatVal, (val) -> {
            option.set(val);
            textField.setText(getStringifiedFloatValue());
        });
    }

    public String getStringifiedFloatValue() {
        float floatVal = getFloatValue();
        String strVal;
        if (floatVal % 1 == 0) {
            strVal = Integer.toString((int) floatVal);
        } else {
            strVal = Float.toString(floatVal);
            strVal = strVal.replaceAll("(\\.\\d\\d\\d)\\d+", "$1");
            strVal = strVal.replaceAll("0+$", "");
        }
        return strVal;
    }

    public float getFloatValue() {
        return ((Number) option.get()).floatValue();
    }

    @Override
    public void render(DrawContext context, int x, int y, int width) {
        super.render(context, x, y, width);

        slider.setValue(getFloatValue());

        int height = getHeight();

        int fullWidth = Math.min(width / 3 - 10, 80);
        int sliderWidth = (fullWidth - 5) * 3 / 4;
        int textFieldWidth = (fullWidth - 5) / 4;

        slider.x = x + width / 6 - fullWidth / 2;
        slider.y = y + height - 7 - 14;
        slider.width = sliderWidth;
        slider.render(context, 0, 0, 0);

        if (textField.getFocus()) {
            textField.setOptions(GuiElementTextField.NO_SPACE | GuiElementTextField.NUM_ONLY);
            textField.setSize(
                    MinecraftClient.getInstance().textRenderer.getWidth(textField.getText()) + 10,
                    16
            );
        } else {
            textField.setText(getStringifiedFloatValue());
            textField.setSize(textFieldWidth, 16);
            textField.setOptions(
                    GuiElementTextField.NO_SPACE | GuiElementTextField.NUM_ONLY | GuiElementTextField.SCALE_TEXT);
        }

        textField.render(context, x + width / 6 - fullWidth / 2 + sliderWidth + 5, y + height - 7 - 14);
    }

    @Override
    public boolean mouseInput(int x, int y, int width, double mouseX, double mouseY, int button) {
        int height = getHeight();

        int fullWidth = Math.min(width / 3 - 10, 80);
        int sliderWidth = (fullWidth - 5) * 3 / 4;
        int textFieldWidth = (fullWidth - 5) / 4;

        slider.x = x + width / 6 - fullWidth / 2;
        slider.y = y + height - 7 - 14;
        slider.width = sliderWidth;
        if (slider.mouseClicked(mouseX, mouseY, button)) {
            slider.setValue(getFloatValue());
            textField.unfocus();
            return true;
        }

        if (textField.getFocus()) {
            textFieldWidth = MinecraftClient.getInstance().textRenderer.getWidth(textField.getText()) + 10;
        }

        int textFieldX = x + width / 6 - fullWidth / 2 + sliderWidth + 5;
        int textFieldY = y + height - 7 - 14;
        textField.setSize(textFieldWidth, 16);

        if (button == 0 || button == 1) {
            if (mouseX > textFieldX && mouseX < textFieldX + textFieldWidth &&
                    mouseY > textFieldY && mouseY < textFieldY + 16) {
                textField.mouseClicked(mouseX, mouseY, button);
                return true;
            }
            textField.unfocus();
        }

        return false;
    }

    @Override
    public boolean mouseReleased(int finalX, int finalY, int finalWidth, double mouseX, double mouseY, int button) {
        return slider.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(int finalX, int finalY, int finalWidth, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (slider.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            slider.setValue(getFloatValue());
            textField.unfocus();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyboardInput(int keyCode, int scanCode, int modifiers) {
        if (textField.getFocus()) {
            textField.keyTyped(keyCode, scanCode, modifiers);

            try {
                textField.setCustomBorderColour(0xffffffff);
                float f = Float.parseFloat(textField.getText());
                if (option.set(f)) {
                    slider.setValue(f);
                } else {
                    textField.setCustomBorderColour(0xff0000ff);
                }
            } catch (Exception e) {
                textField.setCustomBorderColour(0xffff0000);
            }

            return true;
        }
        return false;
    }
}
