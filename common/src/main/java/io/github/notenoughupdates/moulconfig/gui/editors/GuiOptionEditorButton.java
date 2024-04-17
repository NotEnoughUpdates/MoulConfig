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

import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class GuiOptionEditorButton extends ComponentEditor {
    private final int runnableId;
    private String buttonText;
    private final Config config;
    private boolean isUsingRunnable;

    public GuiOptionEditorButton(
        ProcessedOption option,
        int runnableId,
        String buttonText,
        Config config
    ) {
        super(option);
        this.runnableId = runnableId;
        this.config = config;

        this.buttonText = buttonText;
        this.isUsingRunnable = option.getType() == Runnable.class;
        if (this.buttonText == null) this.buttonText = "";
    }

    @Getter
    private final GuiComponent delegate = wrapComponent(new GuiComponent() {
        @Override
        public int getWidth() {
            return 48;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void render(@NotNull GuiImmediateContext context) {

            context.getRenderContext().color(1, 1, 1, 1);
            IMinecraft.instance.bindTexture(GuiTextures.BUTTON);
            context.getRenderContext().drawTexturedRect(0, 0, context.getWidth(), context.getHeight());
            context.getRenderContext().drawStringCenteredScaledMaxWidth(
                buttonText,
                context.getRenderContext().getMinecraft().getDefaultFontRenderer(),
                context.getWidth() / 2f, context.getHeight() / 2f,
                false, context.getWidth() - 4, 0xFF303030
            );
        }

        @Override
        public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
            if (mouseEvent instanceof MouseEvent.Click) {
                val click = (MouseEvent.Click) mouseEvent;
                if (click.getMouseState() && context.isHovered() && click.getMouseButton() == 0) {
                    onClick();
                    return true;
                }
            }
            return super.mouseEvent(mouseEvent, context);
        }
    });

    public void onClick() {
        if (isUsingRunnable) {
            ((Runnable) option.get()).run();
        } else {
            config.executeRunnable(runnableId);
        }
    }

    @Override
    public boolean fulfillsSearch(String word) {
        return super.fulfillsSearch(word) || buttonText.toLowerCase(Locale.ROOT).contains(word);
    }
}
