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

/**/
package io.github.notenoughupdates.moulconfig.processor;

import io.github.notenoughupdates.moulconfig.annotations.*;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.editors.*;
import lombok.val;

import java.lang.reflect.Field;

public class BuiltinMoulConfigGuis {
    public static void addProcessors(MoulConfigProcessor<?> processor) {
        processor.registerConfigEditor(ConfigEditorButton.class, (processedOption, configEditorButton) ->
            new GuiOptionEditorButton(processedOption, configEditorButton.runnableId(), configEditorButton.buttonText(), processedOption.getConfig()));
        processor.registerConfigEditor(ConfigEditorBoolean.class, (processedOption, configEditorBoolean) ->
            new GuiOptionEditorBoolean(processedOption, configEditorBoolean.runnableId(), processedOption.getConfig()));
        processor.registerConfigEditor(ConfigEditorAccordion.class, (processedOption, accordion) ->
            new GuiOptionEditorAccordion(processedOption, accordion.id()));
        processor.registerConfigEditor(ConfigEditorColour.class, (processedOption, configEditorColour) ->
            new GuiOptionEditorColour(processedOption));
        processor.registerConfigEditor(ConfigEditorDropdown.class, (processedOption, configEditorDropdown) ->
            new GuiOptionEditorDropdown(processedOption, configEditorDropdown.values()));
        processor.registerConfigEditor(ConfigEditorSlider.class, (processedOption, configEditorSlider) ->
            new GuiOptionEditorSlider(processedOption, configEditorSlider.minValue(), configEditorSlider.maxValue(), configEditorSlider.minStep()));
        processor.registerConfigEditor(ConfigEditorInfoText.class, (processedOption, configEditorInfoText) ->
            new GuiOptionEditorInfoText(processedOption, configEditorInfoText.infoTitle()));
        processor.registerConfigEditor(ConfigLink.class, ((option, configLink) -> {
            Field field;
            try {
                field = configLink.owner().getField(configLink.field());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            return new GuiOptionEditorButton(option, -1, "Link", option.getConfig()) {
                @Override
                public void onClick() {
                    val linkedOption = activeConfigGUI.getProcessedConfig().getOptionFromField(field);
                    assert linkedOption != null;
                    activeConfigGUI.goToOption(linkedOption);
                }
            };
        }));
        IMinecraft.instance.addExtraBuiltinConfigProcessors(processor);
    }
}
