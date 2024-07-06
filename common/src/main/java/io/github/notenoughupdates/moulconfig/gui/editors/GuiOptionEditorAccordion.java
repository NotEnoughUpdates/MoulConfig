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

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class GuiOptionEditorAccordion extends ComponentEditor {
    private final int accordionId;
    private boolean accordionToggled = false;

    public GuiOptionEditorAccordion(ProcessedOption option, int accordionId) {
        super(option);
        this.accordionId = accordionId;
    }

    private final GuiComponent delegate = new GuiComponent() {
        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public int getHeight() {
            return 20;
        }

        @Override
        public void render(@NotNull GuiImmediateContext context) {
            context.getRenderContext().drawDarkRect(0, 0, context.getWidth(), context.getHeight(), true);
            context.getRenderContext().drawOpenCloseTriangle(accordionToggled, 6, 6, 13.5F - 6F, 13.5F - 6F);
            context.getRenderContext().drawStringScaledMaxWidth(option.getName(), context.getRenderContext().getMinecraft().getDefaultFontRenderer(), 18, 6, false, context.getWidth() - 18, 0xc0c0c0);
        }

        @Override
        public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
            if (mouseEvent instanceof MouseEvent.Click) {
                val click = (MouseEvent.Click) mouseEvent;
                if (click.getMouseState() && context.isHovered() && click.getMouseButton() == 0) {
                    accordionToggled = !accordionToggled;
                    return true;
                }
            }
            return super.mouseEvent(mouseEvent, context);
        }
    };

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public @NotNull GuiComponent getDelegate() {
        return delegate;
    }

    public int getAccordionId() {
        return accordionId;
    }

    public boolean getToggled() {
        return accordionToggled;
    }

    public void setToggled(boolean toggled) {
        accordionToggled = toggled;
    }

}
