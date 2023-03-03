package io.github.moulberry.moulconfig.struct;

import io.github.moulberry.moulconfig.annotations.*;
import io.github.moulberry.moulconfig.gui.*;

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
            new GuiOptionEditorDropdown(processedOption, configEditorDropdown.values(), processedOption.field.getType() == String.class));
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
