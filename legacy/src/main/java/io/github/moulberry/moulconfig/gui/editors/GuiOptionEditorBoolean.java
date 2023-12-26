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

package io.github.moulberry.moulconfig.gui.editors;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.common.IMinecraft;
import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.gui.component.CenterComponent;
import io.github.moulberry.moulconfig.gui.component.PanelComponent;
import io.github.moulberry.moulconfig.gui.component.SwitchComponent;
import io.github.moulberry.moulconfig.observer.GetSetter;
import io.github.moulberry.moulconfig.observer.Property;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiOptionEditorBoolean extends ComponentEditor {

    private final GuiComponent bool;

    public GuiOptionEditorBoolean(
        ProcessedOption option,
        int runnableId,
        Config config
    ) {
        super(option);
        var prop = Property.upgrade((GetSetter<Boolean>) option.intoProperty());
        prop.whenChanged((oldValue, newValue) -> config.executeRunnable(runnableId));
        bool = wrapComponent(new CenterComponent(new SwitchComponent(prop, 200)));
    }

    private class EditorComponentWrapper extends PanelComponent {
        public EditorComponentWrapper(GuiComponent component) {
            super(component);
        }

        @Override
        public int getWidth() {
            return super.getWidth() + 150;
        }

        @Override
        public int getHeight() {
            var fr = IMinecraft.instance.getDefaultFontRenderer();
            return Math.max(45, fr.splitText(option.desc, 250 * 2 / 3 - 10).size() * (fr.getHeight() + 1) + 10);
        }

        @Override
        protected GuiImmediateContext getChildContext(GuiImmediateContext context) {
            return context.translated(5, 13, context.getWidth() / 3 - 10, context.getHeight() - 13);
        }

        @Override
        public void render(@NotNull GuiImmediateContext context) {
            context.getRenderContext().drawDarkRect(0, 0, context.getWidth(), context.getHeight() - 2);

            renderTitle(context);

            renderDescription(context);

            renderElement(context);
        }

        private void renderElement(@NotNull GuiImmediateContext context) {
            context.getRenderContext().pushMatrix();
            context.getRenderContext().translate(5, 13, 0);
            this.getElement().render(getChildContext(context));
            context.getRenderContext().popMatrix();
        }

        private void renderTitle(@NotNull GuiImmediateContext context) {
            int width = context.getWidth();
            var minecraft = context.getRenderContext().getMinecraft();
            var fr = minecraft.getDefaultFontRenderer();
            context.getRenderContext().drawStringCenteredScaledMaxWidth(
                option.name, fr, width / 6, 13, true, width / 3 - 10, 0xc0c0c0
            );
        }

        private void renderDescription(@NotNull GuiImmediateContext context) {
            int width = context.getWidth();
            var minecraft = context.getRenderContext().getMinecraft();
            var fr = minecraft.getDefaultFontRenderer();
            float scale = 1;
            List<String> lines;
            while (true) {
                lines = fr.splitText(option.desc, (int) (width * 2 / 3 / scale - 10));
                if (lines.size() * scale * (fr.getHeight() + 1) + 10 < context.getHeight())
                    break;
                scale -= 1 / 8f;
                if (scale < 1 / 16f) break;
            }
            context.getRenderContext().pushMatrix();
            context.getRenderContext().translate(5 + width / 3, 5, 0);
            context.getRenderContext().scale(scale, scale, 1);
            context.getRenderContext().translate(0, ((context.getHeight() - 10) - (fr.getHeight() + 1) * (lines.size() - 1) * scale) / 2F, 0);
            for (String line : lines) {
                context.getRenderContext().drawString(fr, line, 0, 0, 0xc0c0c0, false);
                context.getRenderContext().translate(0, fr.getHeight() + 1, 0);
            }
            context.getRenderContext().popMatrix();
        }
    }

    protected GuiComponent wrapComponent(GuiComponent component) {
        return new EditorComponentWrapper(
            new CenterComponent(component)
        );
    }

    @Override
    public @NotNull GuiComponent getDelegate() {
        return bool;
    }

}
