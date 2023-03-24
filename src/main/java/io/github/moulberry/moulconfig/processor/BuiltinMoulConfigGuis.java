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
package io.github.moulberry.moulconfig.processor;

import io.github.moulberry.moulconfig.annotations.*;
import io.github.moulberry.moulconfig.gui.editors.*;

public class BuiltinMoulConfigGuis {
    public static void addProcessors(MoulConfigProcessor<?> processor) {
        processor.registerConfigEditor(ConfigEditorKeybind.class, (processedOption, keybind) ->
            new GuiOptionEditorKeybind(processedOption, keybind.defaultKey()));
        processor.registerConfigEditor(ConfigEditorButton.class, (processedOption, configEditorButton) ->
            new GuiOptionEditorButton(processedOption, configEditorButton.runnableId(), configEditorButton.buttonText(), processedOption.config));
        processor.registerConfigEditor(ConfigEditorInfoText.class, (processedOption, configEditorInfoText) ->
            new GuiOptionEditorInfoText(processedOption, configEditorInfoText.infoTitle()));
        processor.registerConfigEditor(ConfigEditorBoolean.class, (processedOption, configEditorBoolean) ->
            new GuiOptionEditorBoolean(processedOption, configEditorBoolean.runnableId(), processedOption.config));
        processor.registerConfigEditor(ConfigEditorAccordion.class, (processedOption, accordion) ->
            new GuiOptionEditorAccordion(processedOption, accordion.id()));
        processor.registerConfigEditor(ConfigEditorDropdown.class, (processedOption, configEditorDropdown) ->
            new GuiOptionEditorDropdown(processedOption, configEditorDropdown.values()));
        processor.registerConfigEditor(ConfigEditorDraggableList.class, (processedOption, configEditorDraggableList) ->
            new GuiOptionEditorDraggableList(processedOption, configEditorDraggableList.exampleText(), configEditorDraggableList.allowDeleting()));
        processor.registerConfigEditor(ConfigEditorColour.class, (processedOption, configEditorColour) ->
            new GuiOptionEditorColour(processedOption));
        processor.registerConfigEditor(ConfigEditorText.class, (processedOption, configEditorText) ->
            new GuiOptionEditorText(processedOption));
        processor.registerConfigEditor(ConfigEditorSlider.class, (processedOption, configEditorSlider) ->
            new GuiOptionEditorSlider(processedOption, configEditorSlider.minValue(), configEditorSlider.maxValue(), configEditorSlider.minStep()));
    }
}
