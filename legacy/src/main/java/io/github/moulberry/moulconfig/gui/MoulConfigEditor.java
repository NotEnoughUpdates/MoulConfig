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

package io.github.moulberry.moulconfig.gui;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.Social;
import io.github.moulberry.moulconfig.common.IMinecraft;
import io.github.moulberry.moulconfig.gui.editors.GuiOptionEditorAccordion;
import io.github.moulberry.moulconfig.gui.elements.GuiElementTextField;
import io.github.moulberry.moulconfig.internal.*;
import io.github.moulberry.moulconfig.processor.MoulConfigProcessor;
import io.github.moulberry.moulconfig.processor.ProcessedCategory;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Collectors;


public class MoulConfigEditor<T extends Config> extends GuiElement {
    private final long openedMillis;
    private final LerpingInteger optionsScroll = new LerpingInteger(0, 150);
    private final LerpingInteger categoryScroll = new LerpingInteger(0, 150);
    private final MoulConfigProcessor<T> processedConfig;
    private final LerpingInteger minimumSearchSize = new LerpingInteger(0, 150);
    private final GuiElementTextField searchField = new GuiElementTextField("", 0, 20, 0);
    private String selectedCategory = null;
    private float optionsBarStart;
    private float optionsBarend;
    private int lastMouseX = 0;
    private int keyboardScrollXCutoff = 0;
    private boolean showSubcategories = true;

    private LinkedHashMap<String, ProcessedCategory> currentlyVisibleCategories;
    private Set<ProcessedOption> currentlyVisibleOptions;
    private Map<String, Set<String>> childCategoryLookup = new HashMap<>();
    private List<ProcessedOption> allOptions = new ArrayList<>();

    public MoulConfigEditor(MoulConfigProcessor<T> processedConfig) {
        this.openedMillis = System.currentTimeMillis();
        this.processedConfig = processedConfig;
        for (Map.Entry<String, ProcessedCategory> category : processedConfig.getAllCategories().entrySet()) {
            allOptions.addAll(category.getValue().options);
            if (category.getValue().parent != null) {
                childCategoryLookup.computeIfAbsent(category.getValue().parent, ignored -> new HashSet<>())
                    .add(category.getKey());
            }
        }
        updateSearchResults();
    }

    private List<ProcessedOption> getOptionsInCategory(ProcessedCategory cat) {
        List<ProcessedOption> options = new ArrayList<>(cat.options);
        options.removeIf(it -> !currentlyVisibleOptions.contains(it));
        return options;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    private void setSelectedCategory(String category) {
        selectedCategory = category;
        optionsScroll.setValue(0);
    }

    public void search(String searchText) {
        searchField.setText(searchText);
        updateSearchResults();
    }


    private void propagateSearchinessForAccordions(Set<ProcessedOption> options, Set<ProcessedOption> lastRound, boolean upwards) {
        if (lastRound.isEmpty()) return;
        options.addAll(lastRound);
        Set<ProcessedOption> nextRound = new HashSet<>();

        for (ProcessedOption option : lastRound) {
            if (option.accordionId >= 0 && upwards) {
                for (ProcessedOption accordion : option.category.options) {
                    if (accordion == option) continue;
                    if (!(accordion.editor instanceof GuiOptionEditorAccordion)) continue;
                    if (((GuiOptionEditorAccordion) accordion.editor).getAccordionId() == option.accordionId) {
                        nextRound.add(accordion);
                    }
                }
            }
            if (option.editor instanceof GuiOptionEditorAccordion && !upwards) {
                int parentId = ((GuiOptionEditorAccordion) option.editor).getAccordionId();
                for (ProcessedOption potentialChild : option.category.options) {
                    if (potentialChild.accordionId == parentId) {
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
            for (ProcessedCategory category : processedConfig.getAllCategories().values()) {
                allOptions.addAll(category.options);
            }
        }
        String toSearch = searchField.getText().trim().toLowerCase(Locale.ROOT);
        if (!toSearch.isEmpty()) {
            Set<ProcessedOption> matchingOptions = new HashSet<>(allOptions);
            for (String word : toSearch.split(" +")) {
                matchingOptions.removeIf(it -> ContextAware.wrapErrorWithContext(it.editor, () -> !it.editor.fulfillsSearch(word)));
            }

            LinkedHashMap<String, ProcessedCategory> directlyMatchedCategories = new LinkedHashMap<>(processedConfig.getAllCategories());
            if (!processedConfig.getConfigObject().shouldSearchCategoryNames()) directlyMatchedCategories.clear();
            for (String word : toSearch.split(" +")) {
                directlyMatchedCategories.entrySet().removeIf(it -> ContextAware.wrapErrorWithContext(it.getValue().reflectField,
                    () -> !(it.getValue().name.toLowerCase(Locale.ROOT).contains(word) || it.getValue().desc.toLowerCase(Locale.ROOT).contains(word))));
            }

            Set<ProcessedOption> matchingOptionsAndDependencies = new HashSet<>();

            // No search propagation needed if category is matched.
            // Add them directly to the final visible option set.
            for (ProcessedCategory directCategory : directlyMatchedCategories.values()) {
                matchingOptionsAndDependencies.addAll(directCategory.options);
                directCategory.options.forEach(matchingOptions::remove);
            }

            propagateSearchinessForAccordions(matchingOptionsAndDependencies, matchingOptions, true);
            propagateSearchinessForAccordions(matchingOptionsAndDependencies, matchingOptions, false);

            currentlyVisibleOptions = matchingOptionsAndDependencies;

            Set<ProcessedCategory> visibleCategories = matchingOptionsAndDependencies.stream().map(it -> it.category).collect(Collectors.toSet());
            Set<ProcessedCategory> parentCategories = visibleCategories.stream()
                .filter(it -> it.parent != null).map(it -> processedConfig.getAllCategories().get(it.parent))
                .filter(Objects::nonNull).collect(Collectors.toSet());
            visibleCategories.addAll(parentCategories);
            LinkedHashMap<String, ProcessedCategory> matchingCategories = new LinkedHashMap<>(processedConfig.getAllCategories());
            matchingCategories.entrySet().removeIf(stringProcessedCategoryEntry -> !visibleCategories.contains(stringProcessedCategoryEntry.getValue()));
            currentlyVisibleCategories = matchingCategories;
        } else {
            currentlyVisibleCategories = processedConfig.getAllCategories();
            currentlyVisibleOptions = new HashSet<>(allOptions);
        }
    }

    public LinkedHashMap<String, ProcessedCategory> getCurrentlyVisibleCategories() {
        var newHashes = new LinkedHashMap<>(currentlyVisibleCategories);
        newHashes.entrySet().removeIf(it -> {
            if (it.getValue().parent == null) return false;
            if (!showSubcategories) return true;
            if (it.getValue().parent.equals(getSelectedCategory())) return false;
            var processedCategory = currentlyVisibleCategories.get(getSelectedCategory());
            if (processedCategory == null) return true;
            //noinspection RedundantIfStatement
            if (it.getValue().parent.equals(processedCategory.parent)) return false;
            return true;
        });
        return newHashes;
    }

    public LinkedHashMap<String, ProcessedCategory> getCurrentlySearchedCategories() {
        return currentlyVisibleCategories;
    }

    public void render() {
        optionsScroll.tick();
        categoryScroll.tick();
        handleKeyboardPresses();

        List<String> tooltipToDisplay = null;

        long currentTime = System.currentTimeMillis();
        long delta = currentTime - openedMillis;

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();
        int mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth;
        int mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1;

        float opacityFactor = LerpUtils.sigmoidZeroOne(delta / 500f);
        RenderUtils.drawGradientRect(0, 0, 0, width, height,
            (int) (0x80 * opacityFactor) << 24 | 0x101010,
            (int) (0x90 * opacityFactor) << 24 | 0x101010
        );

        int xSize = Math.min(scaledResolution.getScaledWidth() - 100 / scaledResolution.getScaleFactor(), 500);
        int ySize = Math.min(scaledResolution.getScaledHeight() - 100 / scaledResolution.getScaleFactor(), 400);

        int x = (scaledResolution.getScaledWidth() - xSize) / 2;
        int y = (scaledResolution.getScaledHeight() - ySize) / 2;

        int adjScaleFactor = Math.max(2, scaledResolution.getScaleFactor());

        int openingXSize = xSize;
        int openingYSize = ySize;
        if (delta < 150) {
            openingXSize = (int) (delta * xSize / 150);
            openingYSize = 5;
        } else if (delta < 300) {
            openingYSize = 5 + (int) (delta - 150) * (ySize - 5) / 150;
        }
        RenderUtils.drawFloatingRectDark(
            (scaledResolution.getScaledWidth() - openingXSize) / 2,
            (scaledResolution.getScaledHeight() - openingYSize) / 2,
            openingXSize, openingYSize
        );
        GlScissorStack.clear();
        GlScissorStack.push((scaledResolution.getScaledWidth() - openingXSize) / 2,
            (scaledResolution.getScaledHeight() - openingYSize) / 2,
            (scaledResolution.getScaledWidth() + openingXSize) / 2,
            (scaledResolution.getScaledHeight() + openingYSize) / 2, scaledResolution
        );

        RenderUtils.drawFloatingRectDark(x + 5, y + 5, xSize - 10, 20, false);

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        TextRenderUtils.drawStringCenteredScaledMaxWidth(
            processedConfig.getConfigObject().getTitle(),
            fr,
            x + xSize / 2,
            y + 15,
            false,
            xSize - processedConfig.getConfigObject().getSocials().size() * 18 * 2 - 25,
            0xa0a0a0
        );

        RenderUtils.drawFloatingRectDark(x + 4, y + 49 - 20,
            140, ySize - 54 + 20, false
        );

        int innerPadding = 20 / adjScaleFactor;
        int innerLeft = x + 4 + innerPadding;
        int innerRight = x + 144 - innerPadding;
        int innerTop = y + 49 + innerPadding;
        int innerBottom = y + ySize - 5 - innerPadding;
        Gui.drawRect(innerLeft, innerTop, innerLeft + 1, innerBottom, 0xff08080E); //Left
        Gui.drawRect(innerLeft + 1, innerTop, innerRight, innerTop + 1, 0xff08080E); //Top
        Gui.drawRect(innerRight - 1, innerTop + 1, innerRight, innerBottom, 0xff28282E); //Right
        Gui.drawRect(innerLeft + 1, innerBottom - 1, innerRight - 1, innerBottom, 0xff28282E); //Bottom
        Gui.drawRect(innerLeft + 1, innerTop + 1, innerRight - 1, innerBottom - 1, 0x6008080E); //Middle

        /// <editor-fold name="Render categories on the left">

        GlScissorStack.push(0, innerTop + 1, scaledResolution.getScaledWidth(),
            innerBottom - 1, scaledResolution
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
            var catName = processedConfig.getConfigObject().formatCategoryName(entry.getValue(), isSelected);
            var align = processedConfig.getConfigObject().alignCategory(entry.getValue(), isSelected);
            var textLength = fr.getStringWidth(catName);
            var isIndented = childCategories != null || entry.getValue().parent != null;
            if (textLength > ((isIndented) ? 90 : 100)) {
                TextRenderUtils.drawStringCenteredScaledMaxWidth(catName,
                    fr, x + 75 + (isIndented ? 5 : 0), y + 70 + catY, false, (isIndented ? 90 : 100), -1
                );
            } else if (align == HorizontalAlign.CENTER) {
                TextRenderUtils.drawStringCenteredScaledMaxWidth(catName,
                    fr, x + 75, y + 70 + catY, false, (isIndented ? 90 : 100), -1
                );
            } else if (align == HorizontalAlign.RIGHT) {
                fr.drawString(catName, x + 75 + 50 - textLength, y + 70 + catY - fr.FONT_HEIGHT / 2, -1, false);
            } else {
                fr.drawString(catName, x + 75 - 50 + (isIndented ? 10 : 0), y + 70 + catY - fr.FONT_HEIGHT / 2, -1, false);
            }
            if (childCategories != null) {
                var isExpanded = showSubcategories && (isSelected || childCategories.contains(getSelectedCategory()));
                RenderUtils.drawOpenCloseTriangle(isExpanded, x + 24.5, y + 67 + catY, 6, 6);
                if (isExpanded)
                    drawVerticalLine(x + 27, y + catY + 76, y + catY + 76 + childCategories.size() * 15, 0xFF444444);
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
        Gui.drawRect(innerLeft + 2, innerTop + 5, innerLeft + 7, innerBottom - 5, 0xff101010);
        Gui.drawRect(innerLeft + 3, innerTop + 6 + (int) (catDist * catBarStart), innerLeft + 6,
            innerTop + 6 + (int) (catDist * catBarEnd), 0xff303030
        );

        GlScissorStack.pop(scaledResolution);
        /// </editor-fold>

        TextRenderUtils.drawStringCenteredScaledMaxWidth("Categories",
            fr, x + 75, y + 44, false, 120, 0xa368ef
        );

        RenderUtils.drawFloatingRectDark(x + 149, y + 29, xSize - 154, ySize - 34, false);

        innerLeft = x + 149 + innerPadding;
        innerRight = x + xSize - 5 - innerPadding;
        innerBottom = y + ySize - 5 - innerPadding;

        IMinecraft.instance.bindTexture(GuiTextures.SEARCH);
        GlStateManager.color(1, 1, 1, 1);
        RenderUtils.drawTexturedRect(innerRight - 20, innerTop - (20 + innerPadding) / 2 - 9, 18, 18, GL11.GL_NEAREST);

        minimumSearchSize.tick();
        boolean shouldShow = !searchField.getText().trim().isEmpty() || searchField.getFocus();
        if (shouldShow && minimumSearchSize.getTarget() < 30) {
            minimumSearchSize.setTarget(30);
            minimumSearchSize.resetTimer();
        } else if (!shouldShow && minimumSearchSize.getTarget() > 0) {
            minimumSearchSize.setTarget(0);
            minimumSearchSize.resetTimer();
        }

        int rightStuffLen = 20;
        if (minimumSearchSize.getValue() > 1) {
            int strLen = Minecraft.getMinecraft().fontRendererObj.getStringWidth(searchField.getText()) + 10;
            if (!shouldShow) strLen = 0;

            int len = Math.max(strLen, minimumSearchSize.getValue());
            searchField.setSize(len, 18);
            searchField.render(innerRight - 25 - len, innerTop - (20 + innerPadding) / 2 - 9);

            rightStuffLen += 5 + len;
        }

        if (getSelectedCategory() != null && currentConfigEditing.containsKey(getSelectedCategory())) {
            ProcessedCategory cat = currentConfigEditing.get(getSelectedCategory());

            TextRenderUtils.drawStringScaledMaxWidth(cat.desc,
                fr, innerLeft + 5, y + 40, true, innerRight - innerLeft - rightStuffLen - 10, 0xb0b0b0
            );
        }

        Gui.drawRect(innerLeft, innerTop, innerLeft + 1, innerBottom, 0xff08080E); //Left
        Gui.drawRect(innerLeft + 1, innerTop, innerRight, innerTop + 1, 0xff08080E); //Top
        Gui.drawRect(innerRight - 1, innerTop + 1, innerRight, innerBottom, 0xff303036); //Right
        Gui.drawRect(innerLeft + 1, innerBottom - 1, innerRight - 1, innerBottom, 0xff303036); //Bottom
        Gui.drawRect(innerLeft + 1, innerTop + 1, innerRight - 1, innerBottom - 1, 0x6008080E); //Middle

        /// <editor-fold name="Render options on the right">
        GlScissorStack.push(innerLeft + 1, innerTop + 1, innerRight - 1, innerBottom - 1, scaledResolution);
        float barSize = 1;
        int optionY = -optionsScroll.getValue();
        if (getSelectedCategory() != null && currentConfigEditing.containsKey(getSelectedCategory())) {
            ProcessedCategory cat = currentConfigEditing.get(getSelectedCategory());
            int optionWidthDefault = innerRight - innerLeft - 20;
            GlStateManager.enableDepth();
            HashMap<Integer, Integer> activeAccordions = new HashMap<>();
            for (ProcessedOption option : getOptionsInCategory(cat)) {

                int optionWidth = optionWidthDefault;
                if (option.accordionId >= 0) {
                    if (!activeAccordions.containsKey(option.accordionId)) {
                        continue;
                    }
                    int accordionDepth = activeAccordions.get(option.accordionId);
                    optionWidth = optionWidthDefault - (2 * innerPadding) * (accordionDepth + 1);
                }

                GuiOptionEditor editor = option.editor;
                if (editor == null) {
                    continue;
                }
                if (editor instanceof GuiOptionEditorAccordion) {
                    GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                    if (accordion.getToggled()) {
                        int accordionDepth = 0;
                        if (option.accordionId >= 0) {
                            accordionDepth = activeAccordions.get(option.accordionId) + 1;
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
                        editor.render(finalX, finalY, finalOptionWidth);
                        return null;
                    });
                }
                optionY += optionHeight + 5;
            }
            GlStateManager.disableDepth();
            if (optionY > 0) {
                barSize =
                    LerpUtils.clampZeroOne((float) (innerBottom - innerTop - 2) / (optionY + 5 + optionsScroll.getValue()));
            }
        }

        GlScissorStack.pop(scaledResolution);

        /// </editor-fold>

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (getSelectedCategory() != null && currentConfigEditing.containsKey(getSelectedCategory())) {
            int optionYOverlay = -optionsScroll.getValue();
            ProcessedCategory cat = currentConfigEditing.get(getSelectedCategory());
            int optionWidthDefault = innerRight - innerLeft - 20;

            GlStateManager.translate(0, 0, 10);
            GlStateManager.enableDepth();
            HashMap<Integer, Integer> activeAccordions = new HashMap<>();
            for (ProcessedOption option : getOptionsInCategory(cat)) {
                int optionWidth = optionWidthDefault;
                if (option.accordionId >= 0) {
                    if (!activeAccordions.containsKey(option.accordionId)) {
                        continue;
                    }
                    int accordionDepth = activeAccordions.get(option.accordionId);
                    optionWidth = optionWidthDefault - (2 * innerPadding) * (accordionDepth + 1);
                }

                GuiOptionEditor editor = option.editor;
                if (editor == null) {
                    continue;
                }
                if (editor instanceof GuiOptionEditorAccordion) {
                    GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                    if (accordion.getToggled()) {
                        int accordionDepth = 0;
                        if (option.accordionId >= 0) {
                            accordionDepth = activeAccordions.get(option.accordionId) + 1;
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
                            finalX,
                            finalY,
                            finalOptionWidth
                        );
                        return null;
                    });
                }
                optionYOverlay += optionHeight + 5;
            }
            GlStateManager.disableDepth();
            GlStateManager.translate(0, 0, -10);
        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

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
        Gui.drawRect(innerRight - 10, innerTop + 5, innerRight - 5, innerBottom - 5, 0xff101010);
        Gui.drawRect(
            innerRight - 9,
            innerTop + 6 + (int) (dist * optionsBarStart),
            innerRight - 6,
            innerTop + 6 + (int) (dist * optionsBarend),
            0xff303030
        );

        List<Social> socials = processedConfig.getConfigObject().getSocials();
        for (int socialIndex = 0; socialIndex < socials.size(); socialIndex++) {
            Social social = socials.get(socialIndex);
            Minecraft.getMinecraft().getTextureManager().bindTexture(social.getIcon());
            GlStateManager.color(1, 1, 1, 1);
            int socialLeft = x + xSize - 23 - 18 * socialIndex;
            RenderUtils.drawTexturedRect(socialLeft, y + 7, 16, 16, GL11.GL_LINEAR);

            if (mouseX >= socialLeft && mouseX <= socialLeft + 16 &&
                mouseY >= y + 6 && mouseY <= y + 23) {
                tooltipToDisplay = social.getTooltip();
            }
        }

        GlScissorStack.clear();

        if (tooltipToDisplay != null) {
            TextRenderUtils.drawHoveringText(tooltipToDisplay, mouseX, mouseY, width, height, -1, fr);
        }

        GlStateManager.translate(0, 0, -2);
    }

    public boolean mouseInput(int mouseX, int mouseY) {
        lastMouseX = mouseX;
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();

        int xSize = Math.min(width - 100 / scaledResolution.getScaleFactor(), 500);
        int ySize = Math.min(height - 100 / scaledResolution.getScaleFactor(), 400);

        int x = (scaledResolution.getScaledWidth() - xSize) / 2;
        int y = (scaledResolution.getScaledHeight() - ySize) / 2;

        int adjScaleFactor = Math.max(2, scaledResolution.getScaleFactor());

        int innerPadding = 20 / adjScaleFactor;
        int innerTop = y + 49 + innerPadding;
        int innerBottom = y + ySize - 5 - innerPadding;
        int innerLeft = x + 149 + innerPadding;
        int innerRight = x + xSize - 5 - innerPadding;

        int dist = innerBottom - innerTop - 12;
        int optionsBarStartY = innerTop + 6 + (int) (dist * optionsBarStart);
        int optionsBarEndY = innerTop + 6 + (int) (dist * optionsBarend);
        int optionsBarStartX = innerRight - 12;
        int optionsBarEndX = innerRight - 3;

        int categoryY = -categoryScroll.getValue();
        categoryY += 15 * getCurrentlyVisibleCategories().size();
        int catDist = innerBottom - innerTop - 12;
        float catBarStart = categoryScroll.getValue() / (float) (categoryY + categoryScroll.getValue());
        float categoryBarSize = LerpUtils.clampZeroOne(
            (float) (innerBottom - innerTop - 2) / (categoryY + 5 + categoryScroll.getValue()));
        float catBarEnd = catBarStart + categoryBarSize;
        int categoryBarStartY = innerTop + 6 + (int) (catDist * catBarStart);
        int categoryBarEndY = innerTop + 6 + (int) (catDist * catBarEnd);
        int categoryBarStartX = x + innerPadding + 7;
        int categoryBarEndX = x + innerPadding + 12;
        keyboardScrollXCutoff = innerLeft - 10;
        if (Mouse.getEventButtonState()) {
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

            searchField.setFocus(mouseX >= innerRight - 20 && mouseX <= innerRight - 2 &&
                mouseY >= innerTop - (20 + innerPadding) / 2 - 9 && mouseY <= innerTop - (20 + innerPadding) / 2 + 9);

            if (minimumSearchSize.getValue() > 1) {
                int strLen = Minecraft.getMinecraft().fontRendererObj.getStringWidth(searchField.getText()) + 10;
                int len = Math.max(strLen, minimumSearchSize.getValue());

                if (mouseX >= innerRight - 25 - len && mouseX <= innerRight - 25 &&
                    mouseY >= innerTop - (20 + innerPadding) / 2 - 9 && mouseY <= innerTop - (20 + innerPadding) / 2 + 9) {
                    String old = searchField.getText();
                    searchField.mouseClicked(mouseX, mouseY, Mouse.getEventButton());

                    if (!searchField.getText().equals(old)) updateSearchResults();
                }
            }
        }

        int dWheel = Mouse.getEventDWheel();
        if (mouseY > innerTop && mouseY < innerBottom && dWheel != 0) {
            if (dWheel < 0) {
                dWheel = -1;
            }
            if (dWheel > 0) {
                dWheel = 1;
            }
            if (mouseX < innerLeft) {
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
                        if (option.accordionId >= 0) {
                            if (!activeAccordions.containsKey(option.accordionId)) {
                                continue;
                            }
                        }

                        GuiOptionEditor editor = option.editor;
                        if (editor == null) {
                            continue;
                        }
                        if (editor instanceof GuiOptionEditorAccordion) {
                            GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                            if (accordion.getToggled()) {
                                int accordionDepth = 0;
                                if (option.accordionId >= 0) {
                                    accordionDepth = activeAccordions.get(option.accordionId) + 1;
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
                    150,
                    Math.max(10, 5 * Math.abs(newTarget - optionsScroll.getValue()))
                ));
                optionsScroll.resetTimer();
                optionsScroll.setTarget(newTarget);
            }
        } else if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
            if (getCurrentlyVisibleCategories() != null) {
                int catY = -categoryScroll.getValue();
                for (Map.Entry<String, ProcessedCategory> entry : getCurrentlyVisibleCategories().entrySet()) {
                    if (getSelectedCategory() == null) {
                        setSelectedCategory(entry.getKey());
                    }
                    if (mouseX >= x + 5 && mouseX <= x + 145 &&
                        mouseY >= y + 70 + catY - 7 && mouseY <= y + 70 + catY + 7) {
                        if (entry.getKey().equals(getSelectedCategory())) {
                            if (entry.getValue().parent == null)
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

            List<Social> socials = processedConfig.getConfigObject().getSocials();
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
            int optionWidthDefault = innerRight - innerLeft - 20;
            ProcessedCategory cat = getCurrentlyVisibleCategories().get(getSelectedCategory());
            HashMap<Integer, Integer> activeAccordions = new HashMap<>();
            for (ProcessedOption option : getOptionsInCategory(cat)) {
                int optionWidth = optionWidthDefault;
                if (option.accordionId >= 0) {
                    if (!activeAccordions.containsKey(option.accordionId)) {
                        continue;
                    }
                    int accordionDepth = activeAccordions.get(option.accordionId);
                    optionWidth = optionWidthDefault - (2 * innerPadding) * (accordionDepth + 1);
                }

                GuiOptionEditor editor = option.editor;
                if (editor == null) {
                    continue;
                }
                if (editor instanceof GuiOptionEditorAccordion) {
                    GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                    if (accordion.getToggled()) {
                        int accordionDepth = 0;
                        if (option.accordionId >= 0) {
                            accordionDepth = activeAccordions.get(option.accordionId) + 1;
                        }
                        activeAccordions.put(accordion.getAccordionId(), accordionDepth);
                    }
                }
                int finalX = (innerLeft + innerRight - optionWidth) / 2 - 5;
                int finalY = innerTop + 5 + optionY;
                int finalWidth = optionWidth;
                if (ContextAware.wrapErrorWithContext(editor, () -> editor.mouseInputOverlay(
                    finalX,
                    finalY,
                    finalWidth,
                    mouseX,
                    mouseY
                ))) {
                    return true;
                }
                optionY += ContextAware.wrapErrorWithContext(editor, editor::getHeight) + 5;
            }
        }

        if (mouseX > innerLeft && mouseX < innerRight &&
            mouseY > innerTop && mouseY < innerBottom) {
            optionY = -optionsScroll.getValue();
            if (getSelectedCategory() != null && getCurrentlyVisibleCategories() != null &&
                getCurrentlyVisibleCategories().containsKey(getSelectedCategory())) {
                int optionWidthDefault = innerRight - innerLeft - 20;
                ProcessedCategory cat = getCurrentlyVisibleCategories().get(getSelectedCategory());
                HashMap<Integer, Integer> activeAccordions = new HashMap<>();
                for (ProcessedOption option : getOptionsInCategory(cat)) {
                    int optionWidth = optionWidthDefault;
                    if (option.accordionId >= 0) {
                        if (!activeAccordions.containsKey(option.accordionId)) {
                            continue;
                        }
                        int accordionDepth = activeAccordions.get(option.accordionId);
                        optionWidth = optionWidthDefault - (2 * innerPadding) * (accordionDepth + 1);
                    }

                    GuiOptionEditor editor = option.editor;
                    if (editor == null) {
                        continue;
                    }
                    if (editor instanceof GuiOptionEditorAccordion) {
                        GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                        if (accordion.getToggled()) {
                            int accordionDepth = 0;
                            if (option.accordionId >= 0) {
                                accordionDepth = activeAccordions.get(option.accordionId) + 1;
                            }
                            activeAccordions.put(accordion.getAccordionId(), accordionDepth);
                        }
                    }
                    int finalX = (innerLeft + innerRight - optionWidth) / 2 - 5;
                    int finalY = innerTop + 5 + optionY;
                    int finalWidth = optionWidth;
                    if (ContextAware.wrapErrorWithContext(editor, () -> editor.mouseInput(
                        finalX,
                        finalY,
                        finalWidth,
                        mouseX,
                        mouseY
                    ))) {
                        return true;
                    }
                    optionY += ContextAware.wrapErrorWithContext(editor, editor::getHeight) + 5;
                }
            }
        }

        return true;
    }

    public boolean keyboardInput() {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int width = scaledResolution.getScaledWidth();

        int xSize = Math.min(width - 100 / scaledResolution.getScaleFactor(), 500);

        int adjScaleFactor = Math.max(2, scaledResolution.getScaleFactor());

        int innerPadding = 20 / adjScaleFactor;
        int innerWidth = xSize - 154 - innerPadding * 2;

        if (getSelectedCategory() != null && getCurrentlyVisibleCategories() != null &&
            getCurrentlyVisibleCategories().containsKey(getSelectedCategory())) {
            ProcessedCategory cat = getCurrentlyVisibleCategories().get(getSelectedCategory());
            HashMap<Integer, Integer> activeAccordions = new HashMap<>();
            for (ProcessedOption option : getOptionsInCategory(cat)) {
                if (option.accordionId >= 0) {
                    if (!activeAccordions.containsKey(option.accordionId)) {
                        continue;
                    }
                }

                GuiOptionEditor editor = option.editor;
                if (editor == null) {
                    continue;
                }
                if (editor instanceof GuiOptionEditorAccordion) {
                    GuiOptionEditorAccordion accordion = (GuiOptionEditorAccordion) editor;
                    if (accordion.getToggled()) {
                        int accordionDepth = 0;
                        if (option.accordionId >= 0) {
                            accordionDepth = activeAccordions.get(option.accordionId) + 1;
                        }
                        activeAccordions.put(accordion.getAccordionId(), accordionDepth);
                    }
                }
                if (ContextAware.wrapErrorWithContext(editor, editor::keyboardInput)) {
                    return true;
                }
            }
        }

        if (Keyboard.getEventKeyState()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_F)) {
                searchField.setFocus(!searchField.getFocus());
                return true;
            }

            if (!searchField.getFocus() && (!Character.isISOControl(Keyboard.getEventCharacter()) && processedConfig.getConfigObject().shouldAutoFocusSearchbar())) {
                searchField.setFocus(true);
            }

            String old = searchField.getText();
            searchField.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());

            if (!searchField.getText().equals(old)) {
                searchField.setText(Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(
                    searchField.getText(),
                    innerWidth / 2 - 20
                ));
                updateSearchResults();
                return true;
            }
        }

        return false;
    }

    private void handleKeyboardPresses() {
        LerpingInteger target = lastMouseX < keyboardScrollXCutoff ? categoryScroll : optionsScroll;
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            target.setTimeToReachTarget(50);
            target.resetTimer();
            target.setTarget(target.getTarget() + 5);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            target.setTimeToReachTarget(50);
            target.resetTimer();
            if (target.getTarget() >= 0) {
                target.setTarget(target.getTarget() - 5);
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            processedConfig.getConfigObject().saveNow();
        }
    }
}
