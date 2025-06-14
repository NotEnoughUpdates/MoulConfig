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

package io.github.notenoughupdates.moulconfig.gui;

import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.Social;
import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.component.MetaComponent;
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorAccordion;
import io.github.notenoughupdates.moulconfig.internal.ContextAware;
import io.github.notenoughupdates.moulconfig.internal.InitUtil;
import io.github.notenoughupdates.moulconfig.internal.LerpUtils;
import io.github.notenoughupdates.moulconfig.internal.LerpingInteger;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import io.github.notenoughupdates.moulconfig.processor.ProcessedCategory;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOptionImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class MoulConfigEditor<T extends Config> extends GuiElement {
    private final long openedMillis;
    private final LerpingInteger optionsScroll = new LerpingInteger(0, 150);
    private final LerpingInteger categoryScroll = new LerpingInteger(0, 150);
    private final LerpingInteger minimumSearchSize = new LerpingInteger(0, 150);
    private final GetSetter<String> searchFieldContent = GetSetter.floating("");
    private final ClassResizableTextField searchField = new ClassResizableTextField(searchFieldContent);
    @Getter
    private String selectedCategory = null;
    private float optionsBarStart;
    private float optionsBarend;
    private int lastMouseX = 0;
    private int keyboardScrollXCutoff = 0;
    private boolean showSubcategories = true;

    @Setter
    private SearchFunction searchFunction = GuiOptionEditor::fulfillsSearch;

    @Setter
    public Boolean wide = false;

    private LinkedHashMap<String, ? extends ProcessedCategory> currentlyVisibleCategories;
    private Set<ProcessedOption> currentlyVisibleOptions;
    private Map<String, Set<String>> childCategoryLookup = new HashMap<>();
    @Getter
    private List<ProcessedOption> allOptions = new ArrayList<>();
    @Getter
    private final @Unmodifiable LinkedHashMap<String, ? extends ProcessedCategory> allCategories;
    @Getter
    private final @Unmodifiable T configObject;
    private Map<Field, ProcessedOption> optionLookup = new HashMap<>();

    public MoulConfigEditor(MoulConfigProcessor<T> processedConfig) {
        this(
            InitUtil.run(processedConfig.getAllCategories(), processedConfig::requireFinalized),
            processedConfig.getConfigObject());
    }

    public MoulConfigEditor(
        @Unmodifiable LinkedHashMap<String, ? extends ProcessedCategory> allCategories,
        T configObject) {
        this.openedMillis = System.currentTimeMillis();
        this.allCategories = allCategories;
        this.configObject = configObject;
        for (Map.Entry<String, ? extends ProcessedCategory> category : allCategories.entrySet()) {
            allOptions.addAll(category.getValue().getOptions());
            if (category.getValue().getParentCategoryId() != null) {
                childCategoryLookup.computeIfAbsent(category.getValue().getParentCategoryId(), ignored -> new HashSet<>())
                                   .add(category.getKey());
            }
        }
        for (ProcessedOption option : allOptions) {
            option.getEditor().activeConfigGUI = this;
            if (option instanceof ProcessedOption.HasField) {
                optionLookup.put(((ProcessedOption.HasField) option).getField(), option);
            }
        }
        updateSearchResults();
        searchField.setContext(guiContext);
    }

    private List<ProcessedOption> getOptionsInCategory(ProcessedCategory cat) {
        List<ProcessedOption> options = new ArrayList<>(cat.getOptions());
        options.removeIf(it -> !currentlyVisibleOptions.contains(it));
        return options;
    }

    /**
     * Finds a option that was declared from a given field, if that option implements {@link ProcessedOption.HasField}.
     * All options created by {@link MoulConfigProcessor} have fields associated with them. Multiple fields can map to the same option,
     * in which case any one of them could be returned.
     */
    public @Nullable ProcessedOption getOptionFromField(Field field) {
        return optionLookup.get(field);
    }

    public boolean scrollOptionIntoView(ProcessedOption searchedOption, int timeToReachTargetMs) {
        ProcessedCategory processedCategory = getCurrentlySearchedCategories().get(getSelectedCategory());

        // Check we are in the right category
        if (processedCategory != searchedOption.getCategory()) {
            return false;
        }

        // Recursively expand accordions this option is in
        var accordionP = searchedOption;
        while (accordionP.getAccordionId() >= 0) {
            accordionP = processedCategory.getAccordionAnchors().get(accordionP.getAccordionId());
            ((GuiOptionEditorAccordion) accordionP.getEditor()).setToggled(true);
        }

        // If this option is an accordion, also expand that one
        if (searchedOption.getEditor() instanceof GuiOptionEditorAccordion) {
            ((GuiOptionEditorAccordion) searchedOption.getEditor()).setToggled(true);
        }

        // Iterate over all options to find the correct y value for our thingy
        Set<Integer> activeAccordions = new HashSet<>();
        int optionY = 0;
        for (ProcessedOption processedOption : getOptionsInCategory(processedCategory)) {
            val editor = processedOption.getEditor();
            if (editor == null) {
                continue;
            }
            if (processedOption.getAccordionId() >= 0 && !activeAccordions.contains(processedOption.getAccordionId()))
                continue;
            if (editor instanceof GuiOptionEditorAccordion) {
                GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                if (accordion.getToggled()) {
                    activeAccordions.add(accordion.getAccordionId());
                }
            }
            if (processedOption == searchedOption) {
                optionsScroll.setTimeToReachTarget(timeToReachTargetMs);
                optionsScroll.resetTimer();
                optionsScroll.setTarget(optionY);
                return true;
            }
            optionY += ContextAware.wrapErrorWithContext(editor, editor::getHeight) + 5;
        }
        return false;
    }

    public boolean setSelectedCategory(ProcessedCategory category) {
        if (!getCurrentlySearchedCategories().containsKey(category.getIdentifier())) {
            return false;
        }
        setSelectedCategory(category.getIdentifier());
        return true;
    }

    private void setSelectedCategory(String category) {
        selectedCategory = category;
        optionsScroll.setValue(0);
    }

    public void search(String searchText) {
        searchFieldContent.set(searchText);
        updateSearchResults();
    }


    private void propagateSearchinessForAccordions(Set<ProcessedOption> options, Set<ProcessedOption> lastRound, boolean upwards) {
        if (lastRound.isEmpty()) return;
        options.addAll(lastRound);
        Set<ProcessedOption> nextRound = new HashSet<>();

        for (ProcessedOption option : lastRound) {
            if (option.getAccordionId() >= 0 && upwards) {
                for (ProcessedOption accordion : option.getCategory().getOptions()) {
                    if (accordion == option) continue;
                    if (!(accordion.getEditor() instanceof GuiOptionEditorAccordion)) continue;
                    if (((GuiOptionEditorAccordion) accordion.getEditor()).getAccordionId() == option.getAccordionId()) {
                        nextRound.add(accordion);
                    }
                }
            }
            if (option.getEditor() instanceof GuiOptionEditorAccordion && !upwards) {
                int parentId = ((GuiOptionEditorAccordion) option.getEditor()).getAccordionId();
                for (ProcessedOption potentialChild : option.getCategory().getOptions()) {
                    if (potentialChild.getAccordionId() == parentId) {
                        nextRound.add(potentialChild);
                    }
                }
            }
        }

        nextRound.removeAll(options);

        propagateSearchinessForAccordions(options, nextRound, upwards);
    }

    public void updateSearchResults() {
        updateSearchResults(false);
    }

    public void updateSearchResults(boolean recalculateOptionUniverse) {
        showSubcategories = true;
        if (recalculateOptionUniverse) {
            allOptions.clear();
            for (ProcessedCategory category : getAllCategories().values()) {
                allOptions.addAll(category.getOptions());
            }
        }
        String toSearch = searchFieldContent.get().trim().toLowerCase(Locale.ROOT);
        if (!toSearch.isEmpty()) {
            Set<ProcessedOption> matchingOptions = new HashSet<>(allOptions);
            for (String word : toSearch.split(" +")) {
                matchingOptions.removeIf(it -> ContextAware.wrapErrorWithContext(it.getEditor(), () -> !searchFunction.fulfillsSearch(it.getEditor(), word)));
            }

            HashSet<ProcessedCategory> directlyMatchedCategories = new HashSet<>(getAllCategories().values());
            if (!getConfigObject().shouldSearchCategoryNames()) directlyMatchedCategories.clear();
            for (String word : toSearch.split(" +")) {
                directlyMatchedCategories.removeIf(it -> ContextAware.wrapErrorWithContext(it,
                                                                                           () -> !(it.getDisplayName().toLowerCase(Locale.ROOT).contains(word)
                                                                                               || it.getDescription().toLowerCase(Locale.ROOT).contains(word))));
            }

            Set<ProcessedOption> matchingOptionsAndDependencies = new HashSet<>();

            var childCategoriesOfDirectlyMatched = directlyMatchedCategories.stream()
                                                                            .flatMap(it -> childCategoryLookup.getOrDefault(it.getIdentifier(), Collections.emptySet()).stream())
                                                                            .map(getAllCategories()::get)
                                                                            .filter(Objects::nonNull)
                                                                            .collect(Collectors.toList());
            directlyMatchedCategories.addAll(childCategoriesOfDirectlyMatched);

            // No search propagation needed if category is matched.
            // Add them directly to the final visible option set.
            for (ProcessedCategory directCategory : directlyMatchedCategories) {
                matchingOptionsAndDependencies.addAll(directCategory.getOptions());
                directCategory.getOptions().forEach(matchingOptions::remove);
            }

            propagateSearchinessForAccordions(matchingOptionsAndDependencies, matchingOptions, true);
            propagateSearchinessForAccordions(matchingOptionsAndDependencies, matchingOptions, false);

            currentlyVisibleOptions = matchingOptionsAndDependencies;

            Set<ProcessedCategory> visibleCategories = matchingOptionsAndDependencies
                .stream()
                .map(ProcessedOption::getCategory).collect(Collectors.toSet());
            Set<ProcessedCategory> parentCategories = visibleCategories.stream()
                                                                       .filter(it -> it.getParentCategoryId() != null)
                                                                       .map(it -> getAllCategories().get(it.getParentCategoryId()))
                                                                       .filter(Objects::nonNull).collect(Collectors.toSet());
            visibleCategories.addAll(parentCategories);
            LinkedHashMap<String, ProcessedCategory> matchingCategories = new LinkedHashMap<>(getAllCategories());
            matchingCategories.entrySet().removeIf(stringProcessedCategoryEntry -> !visibleCategories.contains(stringProcessedCategoryEntry.getValue()));
            currentlyVisibleCategories = matchingCategories;
        } else {
            currentlyVisibleCategories = getAllCategories();
            currentlyVisibleOptions = new HashSet<>(allOptions);
        }
    }

    public LinkedHashMap<String, ProcessedCategory> getCurrentlyVisibleCategories() {
        var newHashes = new LinkedHashMap<String, ProcessedCategory>(currentlyVisibleCategories);
        newHashes.entrySet().removeIf(it -> {
            if (it.getValue().getParentCategoryId() == null) return false;
            if (!showSubcategories) return true;
            if (it.getValue().getParentCategoryId().equals(getSelectedCategory())) return false;
            var processedCategory = currentlyVisibleCategories.get(getSelectedCategory());
            if (processedCategory == null) return true;
            //noinspection RedundantIfStatement
            if (it.getValue().getParentCategoryId().equals(processedCategory.getParentCategoryId())) return false;
            return true;
        });
        return newHashes;
    }

    public LinkedHashMap<String, ? extends ProcessedCategory> getCurrentlySearchedCategories() {
        return currentlyVisibleCategories;
    }

    public void render() {
        optionsScroll.tick();
        categoryScroll.tick();
        handleKeyboardPresses();

        List<String> tooltipToDisplay = null;

        long currentTime = System.currentTimeMillis();
        long delta = currentTime - openedMillis;

        IMinecraft iMinecraft = IMinecraft.instance;
        RenderContext context = IMinecraft.instance.provideTopLevelRenderContext();

        int width = iMinecraft.getScaledWidth();
        int height = iMinecraft.getScaledHeight();
        int scaleFactor = iMinecraft.getScaleFactor();
        int mouseX = iMinecraft.getMouseX();
        int mouseY = iMinecraft.getMouseY();

        int baseXSize = Math.min(width - 100 / scaleFactor, 500);
        int xSize;
        if (wide) {
            int raw = (int) Math.floor(baseXSize * 1.5f);
            xSize = Math.min(raw, width - 100 / scaleFactor);
        } else {
            xSize = baseXSize;
        }
        int ySize = Math.min(height - 100 / scaleFactor, 400);

        float leftPanelScalar = 1.0f;
        if (wide) {
            leftPanelScalar = 1.25f;
        }
        int leftPanelWidth = (int) (140f * leftPanelScalar);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        int adjScaleFactor = Math.max(2, scaleFactor);

        float opacityFactor = LerpUtils.sigmoidZeroOne(delta / 500f);
        context.drawGradientRect(
            0, 0, 0, width, height,
            (int) (0x80 * opacityFactor) << 24 | 0x101010,
            (int) (0x90 * opacityFactor) << 24 | 0x101010
        );


        int openingXSize = xSize;
        int openingYSize = ySize;
        if (delta < 150) {
            openingXSize = (int) (delta * xSize / 150);
            openingYSize = 5;
        } else if (delta < 300) {
            openingYSize = 5 + (int) (delta - 150) * (ySize - 5) / 150;
        }
        context.drawDarkRect(
            (width - openingXSize) / 2,
            (height - openingYSize) / 2,
            openingXSize, openingYSize
        );
        context.clearScissor();
        context.pushScissor(
            (width - openingXSize) / 2,
            (height - openingYSize) / 2,
            (width + openingXSize) / 2,
            (height + openingYSize) / 2
        );

        context.drawDarkRect(x + 4, y + 5, xSize - 9, 20, false);

        IFontRenderer ifr = iMinecraft.getDefaultFontRenderer();
        context.drawStringCenteredScaledMaxWidth(
            getConfigObject().getTitle(),
            ifr,
            x + (float) xSize / 2,
            y + 15,
            false,
            xSize - getConfigObject().getSocials().size() * 18 * 2 - 25,
            0xa0a0a0
        );

        context.drawDarkRect(
            x + 4, y + 49 - 20,
            leftPanelWidth, ySize - 54 + 20, false
        );

        int innerPadding = 20 / adjScaleFactor;
        int innerLeft = x + 4 + innerPadding;
        int innerRight = x + (leftPanelWidth + 4) - innerPadding;
        int innerTop = y + 49 + innerPadding;
        int innerBottom = y + ySize - 5 - innerPadding;
        int catInnerW  = innerRight - innerLeft;              // total available width
        int catCenterX = innerLeft + catInnerW / 2;
        context.drawColoredRect(innerLeft, innerTop, innerLeft + 1, innerBottom, 0xff08080E); //Left
        context.drawColoredRect(innerLeft + 1, innerTop, innerRight, innerTop + 1, 0xff08080E); //Top
        context.drawColoredRect(innerRight - 1, innerTop + 1, innerRight, innerBottom, 0xff28282E); //Right
        context.drawColoredRect(innerLeft + 1, innerBottom - 1, innerRight - 1, innerBottom, 0xff28282E); //Bottom
        context.drawColoredRect(innerLeft + 1, innerTop + 1, innerRight - 1, innerBottom - 1, 0x6008080E); //Middle

        /// <editor-fold name="Render categories on the left">

        context.pushScissor(
            0, innerTop + 1,
            width, innerBottom - 1
        );

        float catBarSize = 1;
        int catY = -categoryScroll.getValue();

        LinkedHashMap<String, ProcessedCategory> currentConfigEditing = getCurrentlyVisibleCategories();
        for (Map.Entry<String, ProcessedCategory> entry : currentConfigEditing.entrySet()) {
            String selectedCategory = getSelectedCategory();
            if (selectedCategory == null || !currentConfigEditing.containsKey(selectedCategory)) {
                setSelectedCategory(entry.getKey());
            }
            var isSelected = entry.getKey().equals(getSelectedCategory());
            var childCategories = childCategoryLookup.get(entry.getKey());
            var catName = getConfigObject().formatCategoryName(entry.getValue(), isSelected);
            var align = getConfigObject().alignCategory(entry.getValue(), isSelected);
            var textLength = ifr.getStringWidth(catName);
            var isIndented = childCategories != null || entry.getValue().getParentCategoryId() != null;
            int maxTextLength = (int) (((isIndented) ? 90 : 100) * leftPanelScalar);
            int centerMark = x  + (int) (75 * leftPanelScalar);
            if (textLength > maxTextLength) {
                context.drawStringCenteredScaledMaxWidth(catName,
                                                         ifr, centerMark + (isIndented ? 5 : 0), y + 70 + catY, false, maxTextLength, -1
                );
            } else if (align == HorizontalAlign.CENTER) {
                context.drawStringCenteredScaledMaxWidth(catName,
                                                         ifr, centerMark, y + 70 + catY, false, maxTextLength, -1
                );
            } else if (align == HorizontalAlign.RIGHT) {
                context.drawString(ifr, catName, centerMark + 50 - textLength, y + 70 + catY - ifr.getHeight() / 2, -1, false);
            } else {
                context.drawString(ifr, catName, centerMark - 50 + (isIndented ? 10 : 0), y + 70 + catY - ifr.getHeight() / 2, -1, false);
            }
            if (childCategories != null) {
                var isExpanded = showSubcategories && (isSelected || childCategories.contains(getSelectedCategory()));
                context.drawOpenCloseTriangle(isExpanded, x + 24.5F, y + 67 + catY, 6, 6);
                if (isExpanded) {
                    context.drawVerticalLine(x + 27, y + catY + 76, y + catY + 76 + ((int) childCategories.stream().filter(currentConfigEditing::containsKey).count()) * 15, 0xFF444444);
                }
            }
            catY += 15;
            if (catY > 0) {
                catBarSize =
                    LerpUtils.clampZeroOne((float) (innerBottom - innerTop - 2) / (catY + 5 + categoryScroll.getValue()));
            }
        }

        float catBarStart = categoryScroll.getValue() / (float) (catY + categoryScroll.getValue());
        float catBarEnd = catBarStart + catBarSize;
        if (catBarEnd > 1) {
            catBarEnd = 1;
            if (categoryScroll.getTarget() / (float) (catY + categoryScroll.getValue()) + catBarSize < 1) {
                int target = optionsScroll.getTarget();
                categoryScroll.setValue((int) Math.ceil(
                    (catY + 5 + categoryScroll.getValue()) - catBarSize * (catY + 5 + categoryScroll.getValue())));
                categoryScroll.setTarget(target);
            } else {
                categoryScroll.setValue((int) Math.ceil(
                    (catY + 5 + categoryScroll.getValue()) - catBarSize * (catY + 5 + categoryScroll.getValue())));
            }
        }
        int catDist = innerBottom - innerTop - 12;
        context.drawColoredRect(innerLeft + 2, innerTop + 5, innerLeft + 7, innerBottom - 5, 0xff101010);
        context.drawColoredRect(innerLeft + 3, innerTop + 6 + (int) (catDist * catBarStart), innerLeft + 6,
                                innerTop + 6 + (int) (catDist * catBarEnd), 0xff303030
        );

        context.popScissor();
        /// </editor-fold>

        context.drawStringCenteredScaledMaxWidth(
            "Categories",
            ifr,
            x + 4 + ((float) leftPanelWidth / 2),
            y + 44,
            false,
            120,
            0xa368ef
        );

        context.drawDarkRect(x + (leftPanelWidth + 9), y + 29, xSize - (leftPanelWidth + 14), ySize - 34, false);

        innerLeft = x + (leftPanelWidth + 9) + innerPadding;
        innerRight = x + xSize - 5 - innerPadding;
        innerBottom = y + ySize - 5 - innerPadding;

        context.bindTexture(GuiTextures.SEARCH);
        context.color(1, 1, 1, 1);
        context.drawTexturedRect(innerRight - 20, innerTop - (20 + innerPadding) / 2 - 9, 18, 18);

        minimumSearchSize.tick();
        boolean shouldShow = !searchFieldContent.get().trim().isEmpty() || searchField.isFocused();
        if (shouldShow && minimumSearchSize.getTarget() < 30) {
            minimumSearchSize.setTarget(30);
            minimumSearchSize.resetTimer();
        } else if (!shouldShow && minimumSearchSize.getTarget() > 0) {
            minimumSearchSize.setTarget(0);
            minimumSearchSize.resetTimer();
        }

        int rightStuffLen = 20;
        if (minimumSearchSize.getValue() > 1) {
            int strLen = ifr.getStringWidth(searchFieldContent.get()) + 10;
            if (!shouldShow) strLen = 0;

            int len = Math.max(strLen, minimumSearchSize.getValue());
            searchField.setWidth(len);
            context.pushMatrix();
            context.translate(innerRight - 25 - len, innerTop - (20 + innerPadding) / 2 - 9, 0);
            searchField.render(
                new GuiImmediateContext(context, 0, 0, 0, 0, mouseX, mouseY, mouseX, mouseY, 0F, 0F)
                    .translated(
                        innerRight - 25 - len, innerTop - (20 + innerPadding) / 2 - 9,
                        0, 0
                    ));
            context.popMatrix();
            rightStuffLen += 5 + len;
        }

        if (getSelectedCategory() != null && currentConfigEditing.containsKey(getSelectedCategory())) {
            ProcessedCategory cat = currentConfigEditing.get(getSelectedCategory());

            context.drawStringScaledMaxWidth(
                cat.getDescription(),
                ifr, innerLeft + 1, y + 40, true, innerRight - innerLeft - rightStuffLen - 10, 0xb0b0b0
            );
        }

        context.drawColoredRect(innerLeft, innerTop, innerLeft + 1, innerBottom, 0xff08080E); //Left
        context.drawColoredRect(innerLeft + 1, innerTop, innerRight, innerTop + 1, 0xff08080E); //Top
        context.drawColoredRect(innerRight - 1, innerTop + 1, innerRight, innerBottom, 0xff303036); //Right
        context.drawColoredRect(innerLeft + 1, innerBottom - 1, innerRight - 1, innerBottom, 0xff303036); //Bottom
        context.drawColoredRect(innerLeft + 1, innerTop + 1, innerRight - 1, innerBottom - 1, 0x6008080E); //Middle

        /// <editor-fold name="Render options on the right">
        context.pushScissor(innerLeft + 1, innerTop + 1, innerRight - 1, innerBottom - 1);
        float barSize = 1;
        int optionY = -optionsScroll.getValue();
        if (getSelectedCategory() != null && currentConfigEditing.containsKey(getSelectedCategory())) {
            ProcessedCategory cat = currentConfigEditing.get(getSelectedCategory());
            int optionWidthDefault = innerRight - innerLeft - 20;
            HashMap<Integer, Integer> activeAccordions = new HashMap<>();
            var options = getOptionsInCategory(cat);
            if (options.isEmpty()) {
                var titlePositionX = (innerLeft + innerRight) / 2;
                var titlePositionY = (innerTop + innerBottom) / 3;
                var innerSize = innerRight - innerLeft - 40;
                var titleScale = 2;
                context.pushMatrix();
                context.translate(titlePositionX, titlePositionY, 0);
                context.drawStringCenteredScaledMaxWidth("§7Seems like your search is found in a subcategory.", ifr,
                                                         0,
                                                         titleScale * ifr.getHeight(),
                                                         true, innerSize, -1
                );
                context.drawStringCenteredScaledMaxWidth("§7Check out the subcategories on the left.", ifr,
                                                         0,
                                                         (titleScale + 1) * ifr.getHeight(),
                                                         true, innerSize, -1
                );
                context.scale(titleScale, titleScale, 1);
                context.drawStringCenteredScaledMaxWidth("§7No options found.", ifr,
                                                         0,
                                                         0,
                                                         true, innerSize / titleScale, -1
                );
                context.popMatrix();
            }
            for (ProcessedOption option : options) {

                int optionWidth = optionWidthDefault;
                if (option.getAccordionId() >= 0) {
                    if (!activeAccordions.containsKey(option.getAccordionId())) {
                        continue;
                    }
                    int accordionDepth = activeAccordions.get(option.getAccordionId());
                    optionWidth = optionWidthDefault - (2 * innerPadding) * (accordionDepth + 1);
                }

                GuiOptionEditor editor = option.getEditor();
                if (editor == null) {
                    continue;
                }
                editor.setGuiContext(guiContext);
                if (editor instanceof GuiOptionEditorAccordion) {
                    GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                    if (accordion.getToggled()) {
                        int accordionDepth = 0;
                        if (option.getAccordionId() >= 0) {
                            accordionDepth = activeAccordions.get(option.getAccordionId()) + 1;
                        }
                        activeAccordions.put(accordion.getAccordionId(), accordionDepth);
                    }
                }
                int optionHeight = ContextAware.wrapErrorWithContext(editor, editor::getHeight);
                if (innerTop + 5 + optionY + optionHeight > innerTop + 1 && innerTop + 5 + optionY < innerBottom - 1) {
                    int finalX = (innerLeft + innerRight - optionWidth) / 2 - 5;
                    int finalY = innerTop + 5 + optionY;
                    int finalOptionWidth = optionWidth;
                    ContextAware.wrapErrorWithContext(editor, () -> {
                        editor.render(context, finalX, finalY, finalOptionWidth);
                        return null;
                    });
                }
                optionY += optionHeight + 5;
            }
            context.disableDepth();
            if (optionY > 0) {
                barSize =
                    LerpUtils.clampZeroOne((float) (innerBottom - innerTop - 2) / (optionY + 5 + optionsScroll.getValue()));
            }
        }

        context.popScissor();
        /// </editor-fold>

        /// <editor-fold name="Render overlays for options on the right">
        context.disableScissor();
        if (getSelectedCategory() != null && currentConfigEditing.containsKey(getSelectedCategory())) {
            int optionYOverlay = -optionsScroll.getValue();
            ProcessedCategory cat = currentConfigEditing.get(getSelectedCategory());
            int optionWidthDefault = innerRight - innerLeft - 20;

            context.pushMatrix();
            context.translate(0, 0, 10);
            context.enableDepth();
            HashMap<Integer, Integer> activeAccordions = new HashMap<>();
            for (ProcessedOption option : getOptionsInCategory(cat)) {
                int optionWidth = optionWidthDefault;
                if (option.getAccordionId() >= 0) {
                    if (!activeAccordions.containsKey(option.getAccordionId())) {
                        continue;
                    }
                    int accordionDepth = activeAccordions.get(option.getAccordionId());
                    optionWidth = optionWidthDefault - (2 * innerPadding) * (accordionDepth + 1);
                }

                GuiOptionEditor editor = option.getEditor();
                if (editor == null) {
                    continue;
                }
                editor.setGuiContext(guiContext);
                if (editor instanceof GuiOptionEditorAccordion) {
                    GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                    if (accordion.getToggled()) {
                        int accordionDepth = 0;
                        if (option.getAccordionId() >= 0) {
                            accordionDepth = activeAccordions.get(option.getAccordionId()) + 1;
                        }
                        activeAccordions.put(accordion.getAccordionId(), accordionDepth);
                    }
                }
                int optionHeight = ContextAware.wrapErrorWithContext(editor, editor::getHeight);
                if (innerTop + 5 + optionYOverlay + optionHeight > innerTop + 1 &&
                    innerTop + 5 + optionYOverlay < innerBottom - 1) {
                    int finalX = (innerLeft + innerRight - optionWidth) / 2 - 5;
                    int finalY = innerTop + 5 + optionYOverlay;
                    int finalOptionWidth = optionWidth;
                    ContextAware.wrapErrorWithContext(editor, () -> {
                        editor.renderOverlay(
                            context,
                            finalX,
                            finalY,
                            finalOptionWidth
                        );
                        return null;
                    });
                }
                optionYOverlay += optionHeight + 5;
            }
            context.disableDepth();
            context.popMatrix();
        }
        context.refreshScissor();
        /// </editor-fold>

        optionsBarStart = optionsScroll.getValue() / (float) (optionY + optionsScroll.getValue());
        optionsBarend = optionsBarStart + barSize;
        if (optionsBarend > 1) {
            optionsBarend = 1;
            if (optionsScroll.getTarget() / (float) (optionY + optionsScroll.getValue()) + barSize < 1) {
                int target = optionsScroll.getTarget();
                optionsScroll.setValue((int) Math.ceil(
                    (optionY + 5 + optionsScroll.getValue()) - barSize * (optionY + 5 + optionsScroll.getValue())));
                optionsScroll.setTarget(target);
            } else {
                optionsScroll.setValue((int) Math.ceil(
                    (optionY + 5 + optionsScroll.getValue()) - barSize * (optionY + 5 + optionsScroll.getValue())));
            }
        }
        int dist = innerBottom - innerTop - 12;
        context.drawColoredRect(innerRight - 10, innerTop + 5, innerRight - 5, innerBottom - 5, 0xff101010);
        context.drawColoredRect(
            innerRight - 9,
            innerTop + 6 + (int) (dist * optionsBarStart),
            innerRight - 6,
            innerTop + 6 + (int) (dist * optionsBarend),
            0xff303030
        );

        List<Social> socials = getConfigObject().getSocials();
        for (int socialIndex = 0; socialIndex < socials.size(); socialIndex++) {
            Social social = socials.get(socialIndex);
            context.bindTexture(social.getIcon());
            context.color(1, 1, 1, 1);
            int socialLeft = x + xSize - 23 - 18 * socialIndex;
            context.drawTexturedRect(socialLeft, y + 7, 16, 16);

            if (mouseX >= socialLeft && mouseX <= socialLeft + 16 &&
                mouseY >= y + 6 && mouseY <= y + 23) {
                tooltipToDisplay = social.getTooltip();
            }
        }

        context.clearScissor();

        if (tooltipToDisplay != null) {
            context.scheduleDrawTooltip(tooltipToDisplay);
        }
        context.doDrawTooltip();
    }

    private GuiContext guiContext = new GuiContext(new MetaComponent());

    public boolean mouseInput(int mouseX, int mouseY, MouseEvent mouseEvent) {
        lastMouseX = mouseX;
        val iMinecraft = IMinecraft.instance;
        int width = iMinecraft.getScaledWidth();
        int height = iMinecraft.getScaledHeight();
        int scaleFactor = iMinecraft.getScaleFactor();
        int adjScaleFactor = Math.max(2, scaleFactor);

        int baseXSize = Math.min(width - 100 / scaleFactor, 500);
        int xSize;
        if (wide) {
            int raw = (int) Math.floor(baseXSize * 1.5f);
            xSize = Math.min(raw, width - 100 / scaleFactor);
        } else {
            xSize = baseXSize;
        }
        int ySize = Math.min(height - 100 / scaleFactor, 400);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        int innerPadding = 20 / adjScaleFactor;
        float leftPanelScalar = wide ? 1.25f : 1.0f;
        int leftPanelWidth = (int) (140f * leftPanelScalar);

        int catsInnerLeft = x + 4 + innerPadding;
        int optsInnerLeft = x + (leftPanelWidth + 9) + innerPadding;
        int optsInnerRight = x + xSize - 5 - innerPadding;
        int innerTop = y + 49 + innerPadding;
        int innerBottom = y + ySize - 5 - innerPadding;

        int dist = innerBottom - innerTop - 12;
        int optionsBarStartY = innerTop + 6 + (int) (dist * optionsBarStart);
        int optionsBarEndY = innerTop + 6 + (int) (dist * optionsBarend);
        int optionsBarStartX = optsInnerRight - 12;
        int optionsBarEndX = optsInnerRight - 3;

        int categoryY = -categoryScroll.getValue();
        categoryY += 15 * getCurrentlyVisibleCategories().size();
        int catDist = innerBottom - innerTop - 12;
        float catBarStart = categoryScroll.getValue() / (float) (categoryY + categoryScroll.getValue());
        float categoryBarSize = LerpUtils.clampZeroOne(
            (float) (innerBottom - innerTop - 2) / (categoryY + 5 + categoryScroll.getValue()));
        float catBarEnd = catBarStart + categoryBarSize;
        int categoryBarStartY = innerTop + 6 + (int) (catDist * catBarStart);
        int categoryBarEndY = innerTop + 6 + (int) (catDist * catBarEnd);
        int categoryBarStartX = catsInnerLeft + 3;
        int categoryBarEndX = catsInnerLeft + 8;
        keyboardScrollXCutoff = catsInnerLeft - 10;
        int mouseButton = mouseEvent instanceof MouseEvent.Click ? ((MouseEvent.Click) mouseEvent).getMouseButton() : -1;
        boolean mouseState = mouseEvent instanceof MouseEvent.Click && ((MouseEvent.Click) mouseEvent).getMouseState();
        if (mouseState) {
            if ((mouseY < optionsBarStartY || mouseY > optionsBarEndY) &&
                (mouseX >= optionsBarStartX && mouseX <= optionsBarEndX) && mouseY > innerTop + 6 && mouseY < innerBottom - 6) {
                optionsScroll.setTimeToReachTarget(200);
                optionsScroll.resetTimer();
                optionsScroll.setTarget(mouseY - innerTop);
                return true;
            } else if ((mouseY < categoryBarStartY || mouseY > categoryBarEndY) &&
                (mouseX >= categoryBarStartX && mouseX <= categoryBarEndX) && mouseY > innerTop + 6 &&
                mouseY < innerBottom - 6) {
                categoryScroll.setTimeToReachTarget(200);
                categoryScroll.resetTimer();
                categoryScroll.setTarget(mouseY - innerTop);
                return true;
            }
            boolean searchIconFocus = (mouseX >= optsInnerRight - 20 && mouseX <= optsInnerRight - 2 &&
                mouseY >= innerTop - (20 + innerPadding) / 2 - 9 && mouseY <= innerTop - (20 + innerPadding) / 2 + 9);


            searchField.setFocus(searchIconFocus);

            if (searchIconFocus && mouseButton == 1) {
                searchField.getText().set("");
                updateSearchResults();
            }

            if (minimumSearchSize.getValue() > 1) {
                int strLen = iMinecraft.getDefaultFontRenderer().getStringWidth(searchFieldContent.get()) + 10;
                int len = Math.max(strLen, minimumSearchSize.getValue());

                if (mouseX >= optsInnerRight - 25 - len && mouseX <= optsInnerRight - 25 &&
                    mouseY >= innerTop - (20 + innerPadding) / 2 - 9 && mouseY <= innerTop - (20 + innerPadding) / 2 + 9) {

                    if (mouseButton == 1) {
                        searchField.getText().set("");
                        updateSearchResults();
                    }

                    String old = searchFieldContent.get();
                    searchField.mouseEvent(mouseEvent,
                                           new GuiImmediateContext(iMinecraft.provideTopLevelRenderContext(), 0, 0, 0, 0, mouseX, mouseY, mouseX, mouseY, 0F, 0F)
                                               .translated(
                                                   optsInnerRight - 25 - len, innerTop - (20 + innerPadding) / 2 - 9,
                                                   0, 0
                                               ));

                    if (!searchFieldContent.get().equals(old)) updateSearchResults();
                }
            }
        }

        int dWheel = mouseEvent instanceof MouseEvent.Scroll ? ((int) ((MouseEvent.Scroll) mouseEvent).getDWheel()) : 0;
        if (mouseY > innerTop && mouseY < innerBottom && dWheel != 0) {
            if (dWheel < 0) {
                dWheel = -1;
            }
            if (dWheel > 0) {
                dWheel = 1;
            }
            if (mouseX < optsInnerLeft) {
                int newTarget = categoryScroll.getTarget() - dWheel * 30;
                if (newTarget < 0) {
                    newTarget = 0;
                }

                float catBarSize = 1;
                int catY = -newTarget;
                for (Map.Entry<String, ProcessedCategory> entry : getCurrentlyVisibleCategories().entrySet()) {
                    if (getSelectedCategory() == null) {
                        setSelectedCategory(entry.getKey());
                    }

                    catY += 15;
                    if (catY > 0) {
                        catBarSize = LerpUtils.clampZeroOne((float) (innerBottom - innerTop - 2) / (catY + 5 + newTarget));
                    }
                }

                int barMax = (int) Math.floor((catY + 5 + newTarget) - catBarSize * (catY + 5 + newTarget));
                if (newTarget > barMax) {
                    newTarget = barMax;
                }
                categoryScroll.resetTimer();
                categoryScroll.setTarget(newTarget);
            } else {
                int newTarget = optionsScroll.getTarget() - dWheel * 30;
                if (newTarget < 0) {
                    newTarget = 0;
                }

                float barSize = 1;
                int optionY = -newTarget;
                if (getSelectedCategory() != null && getCurrentlyVisibleCategories() != null &&
                    getCurrentlyVisibleCategories().containsKey(getSelectedCategory())) {
                    ProcessedCategory cat = getCurrentlyVisibleCategories().get(getSelectedCategory());
                    HashMap<Integer, Integer> activeAccordions = new HashMap<>();
                    for (ProcessedOption option : getOptionsInCategory(cat)) {
                        if (option.getAccordionId() >= 0) {
                            if (!activeAccordions.containsKey(option.getAccordionId())) {
                                continue;
                            }
                        }

                        GuiOptionEditor editor = option.getEditor();
                        if (editor == null) {
                            continue;
                        }
                        ContextAware.wrapErrorWithContext(editor, () -> {
                            editor.setGuiContext(guiContext);
                            return null;
                        });
                        if (editor instanceof GuiOptionEditorAccordion) {
                            GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                            if (accordion.getToggled()) {
                                int accordionDepth = 0;
                                if (option.getAccordionId() >= 0) {
                                    accordionDepth = activeAccordions.get(option.getAccordionId()) + 1;
                                }
                                activeAccordions.put(accordion.getAccordionId(), accordionDepth);
                            }
                        }
                        optionY += ContextAware.wrapErrorWithContext(editor, editor::getHeight) + 5;

                        if (optionY > 0) {
                            barSize = LerpUtils.clampZeroOne((float) (innerBottom - innerTop - 2) / (optionY + 5 + newTarget));
                        }
                    }
                }

                int barMax = (int) Math.floor((optionY + 5 + newTarget) - barSize * (optionY + 5 + newTarget));
                if (newTarget > barMax) {
                    newTarget = barMax;
                }
                optionsScroll.setTimeToReachTarget(Math.min(
                    (leftPanelWidth + 10),
                    Math.max(10, 5 * Math.abs(newTarget - optionsScroll.getValue()))
                ));
                optionsScroll.resetTimer();
                optionsScroll.setTarget(newTarget);
            }
        } else if (mouseState && ((MouseEvent.Click) mouseEvent).getMouseButton() == 0) {
            if (getCurrentlyVisibleCategories() != null) {
                int catY = -categoryScroll.getValue();
                for (Map.Entry<String, ProcessedCategory> entry : getCurrentlyVisibleCategories().entrySet()) {
                    if (getSelectedCategory() == null) {
                        setSelectedCategory(entry.getKey());
                    }
                    if (mouseX >= x + 5 && mouseX <= x + (leftPanelWidth + 5) &&
                        mouseY >= y + 70 + catY - 7 && mouseY <= y + 70 + catY + 7) {
                        if (entry.getKey().equals(getSelectedCategory())) {
                            if (entry.getValue().getParentCategoryId() == null)
                                showSubcategories = !showSubcategories;
                        } else {
                            showSubcategories = true;
                            setSelectedCategory(entry.getKey());
                        }
                        return true;
                    }
                    catY += 15;
                }
            }

            List<Social> socials = getConfigObject().getSocials();
            for (int socialIndex = 0; socialIndex < socials.size(); socialIndex++) {
                int socialLeft = x + xSize - 23 - 18 * socialIndex;

                if (mouseX >= socialLeft && mouseX <= socialLeft + 16 &&
                    mouseY >= y + 6 && mouseY <= y + 23) {
                    socials.get(socialIndex).onClick();
                    return true;
                }
            }
        }

        int optionY = -optionsScroll.getValue();
        if (getSelectedCategory() != null && getCurrentlyVisibleCategories() != null &&
            getCurrentlyVisibleCategories().containsKey(getSelectedCategory())) {
            int optionWidthDefault = optsInnerRight - optsInnerLeft - 20;
            ProcessedCategory cat = getCurrentlyVisibleCategories().get(getSelectedCategory());
            HashMap<Integer, Integer> activeAccordions = new HashMap<>();
            for (ProcessedOption option : getOptionsInCategory(cat)) {
                int optionWidth = optionWidthDefault;
                if (option.getAccordionId() >= 0) {
                    if (!activeAccordions.containsKey(option.getAccordionId())) {
                        continue;
                    }
                    int accordionDepth = activeAccordions.get(option.getAccordionId());
                    optionWidth = optionWidthDefault - (2 * innerPadding) * (accordionDepth + 1);
                }

                GuiOptionEditor editor = option.getEditor();
                if (editor == null) {
                    continue;
                }
                editor.setGuiContext(guiContext);
                if (editor instanceof GuiOptionEditorAccordion) {
                    GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                    if (accordion.getToggled()) {
                        int accordionDepth = 0;
                        if (option.getAccordionId() >= 0) {
                            accordionDepth = activeAccordions.get(option.getAccordionId()) + 1;
                        }
                        activeAccordions.put(accordion.getAccordionId(), accordionDepth);
                    }
                }
                int finalX = (optsInnerLeft + optsInnerRight - optionWidth) / 2 - 5;
                int finalY = innerTop + 5 + optionY;
                int finalWidth = optionWidth;
                if (ContextAware.wrapErrorWithContext(editor, () -> editor.mouseInputOverlay(
                    finalX,
                    finalY,
                    finalWidth,
                    mouseX,
                    mouseY,
                    mouseEvent
                ))) {
                    return true;
                }
                optionY += ContextAware.wrapErrorWithContext(editor, editor::getHeight) + 5;
            }
        }

        boolean handled = false;
        {
            optionY = -optionsScroll.getValue();
            if (getSelectedCategory() != null && getCurrentlyVisibleCategories() != null &&
                getCurrentlyVisibleCategories().containsKey(getSelectedCategory())) {
                int optionWidthDefault = optsInnerRight - optsInnerLeft - 20;
                ProcessedCategory cat = getCurrentlyVisibleCategories().get(getSelectedCategory());
                HashMap<Integer, Integer> activeAccordions = new HashMap<>();
                for (ProcessedOption option : getOptionsInCategory(cat)) {
                    int optionWidth = optionWidthDefault;
                    if (option.getAccordionId() >= 0) {
                        if (!activeAccordions.containsKey(option.getAccordionId())) {
                            continue;
                        }
                        int accordionDepth = activeAccordions.get(option.getAccordionId());
                        optionWidth = optionWidthDefault - (2 * innerPadding) * (accordionDepth + 1);
                    }

                    GuiOptionEditor editor = option.getEditor();
                    if (editor == null) {
                        continue;
                    }
                    editor.setGuiContext(guiContext);
                    if (editor instanceof GuiOptionEditorAccordion) {
                        GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                        if (accordion.getToggled()) {
                            int accordionDepth = 0;
                            if (option.getAccordionId() >= 0) {
                                accordionDepth = activeAccordions.get(option.getAccordionId()) + 1;
                            }
                            activeAccordions.put(accordion.getAccordionId(), accordionDepth);
                        }
                    }
                    int finalX = (optsInnerLeft + optsInnerRight - optionWidth) / 2 - 5;
                    int finalY = innerTop + 5 + optionY;
                    int finalWidth = optionWidth;
                    if (ContextAware.wrapErrorWithContext(editor, () -> editor.mouseInput(
                        finalX,
                        finalY,
                        finalWidth,
                        mouseX,
                        mouseY,
                        mouseEvent
                    ))) {
                        handled = true;
                    }
                    optionY += ContextAware.wrapErrorWithContext(editor, editor::getHeight) + 5;
                }
            }
        }

        return handled;
    }

    public boolean keyboardInput(KeyboardEvent event) {
        val iMinecraft = IMinecraft.instance;
        int width = iMinecraft.getScaledWidth();
        int height = iMinecraft.getScaledHeight();
        int scaleFactor = iMinecraft.getScaleFactor();

        int baseXSize = Math.min(width - 100 / scaleFactor, 500);
        int xSize;
        if (wide) {
            int raw = (int) Math.floor(baseXSize * 1.5f);
            xSize = Math.min(raw, width - 100 / scaleFactor);
        } else {
            xSize = baseXSize;
        }
        float leftPanelScalar = wide ? 1.25f : 1.0f;
        int leftPanelWidth = (int) (140f * leftPanelScalar);

        int adjScaleFactor = Math.max(2, scaleFactor);

        int innerPadding = 20 / adjScaleFactor;
        int innerWidth = xSize - (leftPanelWidth + 14) - innerPadding * 2;

        if (getSelectedCategory() != null && getCurrentlyVisibleCategories() != null &&
            getCurrentlyVisibleCategories().containsKey(getSelectedCategory())) {
            ProcessedCategory cat = getCurrentlyVisibleCategories().get(getSelectedCategory());
            HashMap<Integer, Integer> activeAccordions = new HashMap<>();
            for (ProcessedOption option : getOptionsInCategory(cat)) {
                if (option.getAccordionId() >= 0) {
                    if (!activeAccordions.containsKey(option.getAccordionId())) {
                        continue;
                    }
                }

                GuiOptionEditor editor = option.getEditor();
                if (editor == null) {
                    continue;
                }
                editor.setGuiContext(guiContext);
                if (editor instanceof GuiOptionEditorAccordion) {
                    GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                    if (accordion.getToggled()) {
                        int accordionDepth = 0;
                        if (option.getAccordionId() >= 0) {
                            accordionDepth = activeAccordions.get(option.getAccordionId()) + 1;
                        }
                        activeAccordions.put(accordion.getAccordionId(), accordionDepth);
                    }
                }
                if (ContextAware.wrapErrorWithContext(editor, () -> editor.keyboardInput(event))) {
                    return true;
                }
            }
        }

        if (event instanceof KeyboardEvent.KeyPressed && ((KeyboardEvent.KeyPressed) event).getPressed()) {
            if (IMinecraft.instance.isKeyboardKeyDown(IMinecraft.instance.getKeyboardConstants().getCtrlLeft())
                && IMinecraft.instance.isKeyboardKeyDown(IMinecraft.instance.getKeyboardConstants().getKeyF())) {
                searchField.setFocus(!searchField.isFocused());
                return true;
            }
        }
        if (event instanceof KeyboardEvent.CharTyped) {
            if (!searchField.isFocused() && (!getConfigObject().shouldAutoFocusSearchbar())) {
                searchField.setFocus(true);
            }
        }

        String old = searchFieldContent.get();
        searchField.keyboardEvent(event,
                                  new GuiImmediateContext(iMinecraft.provideTopLevelRenderContext(), 0, 0, 0, 0, 0, 0, 0, 0, 0F, 0F));

        if (!searchFieldContent.get().equals(old)) {
            searchFieldContent.set(IMinecraft.instance.getDefaultFontRenderer().trimStringToWidth(
                searchFieldContent.get(),
                innerWidth / 2 - 20
            ));
            updateSearchResults();
            return true;
        }

        return false;
    }

    private void handleKeyboardPresses() {
        LerpingInteger target = lastMouseX < keyboardScrollXCutoff ? categoryScroll : optionsScroll;
        if (IMinecraft.instance.isKeyboardKeyDown(IMinecraft.instance.getKeyboardConstants().getDown())) {
            target.setTimeToReachTarget(50);
            target.resetTimer();
            target.setTarget(target.getTarget() + 5);
        } else if (IMinecraft.instance.isKeyboardKeyDown(IMinecraft.instance.getKeyboardConstants().getUp())) {
            target.setTimeToReachTarget(50);
            target.resetTimer();
            if (target.getTarget() >= 0) {
                target.setTarget(Math.max(0, target.getTarget() - 5));
            }
        } else if (IMinecraft.instance.isKeyboardKeyDown(IMinecraft.instance.getKeyboardConstants().getEscape())) {
            getConfigObject().saveNow();
        }
    }

    public boolean goToOption(@NotNull ProcessedOption option) {
        if (!setSelectedCategory(option.getCategory())) {
            search("");
            if (!setSelectedCategory(option.getCategory())) {
                return false;
            }
        }
        if (!scrollOptionIntoView(option, 200)) {
            search("");
            if (!scrollOptionIntoView(option, 200)) {
                return false;
            }
        }
        return true;
    }

}
