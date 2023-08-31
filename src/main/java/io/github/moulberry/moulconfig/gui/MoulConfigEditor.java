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
import io.github.moulberry.moulconfig.gui.editors.GuiOptionEditorAccordion;
import io.github.moulberry.moulconfig.gui.elements.GuiElementTextField;
import io.github.moulberry.moulconfig.internal.*;
import io.github.moulberry.moulconfig.processor.MoulConfigProcessor;
import io.github.moulberry.moulconfig.processor.ProcessedCategory;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
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
    private float optionsBarEnd;
    private double lastMouseX = 0;
    private double keyboardScrollXCutoff = 0;

    private LinkedHashMap<String, ProcessedCategory> currentlyVisibleCategories;
    private Set<ProcessedOption> currentlyVisibleOptions;

    private List<ProcessedOption> allOptions = new ArrayList<>();

    public MoulConfigEditor(MoulConfigProcessor<T> processedConfig) {
        this.openedMillis = System.currentTimeMillis();
        this.processedConfig = processedConfig;
        for (ProcessedCategory category : processedConfig.getAllCategories().values()) {
            allOptions.addAll(category.options);
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
            LinkedHashMap<String, ProcessedCategory> matchingCategories = new LinkedHashMap<>(processedConfig.getAllCategories());
            matchingCategories.entrySet().removeIf(stringProcessedCategoryEntry -> !visibleCategories.contains(stringProcessedCategoryEntry.getValue()));
            currentlyVisibleCategories = matchingCategories;
        } else {
            currentlyVisibleCategories = processedConfig.getAllCategories();
            currentlyVisibleOptions = new HashSet<>(allOptions);
        }
    }

    public LinkedHashMap<String, ProcessedCategory> getCurrentlyVisibleCategories() {
        return currentlyVisibleCategories;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float ignoredDelta) {
        optionsScroll.tick();
        categoryScroll.tick();
        //handleKeyboardPresses();

        List<String> tooltipToDisplay = null;

        long currentTime = System.currentTimeMillis();
        long delta = currentTime - openedMillis;

        float opacityFactor = LerpUtils.sigmoidZeroOne(delta / 500f);
        RenderUtils.drawGradientRect(context, 0, 0, MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight(),
                (int) (0x80 * opacityFactor) << 24 | 0x101010,
                (int) (0x90 * opacityFactor) << 24 | 0x101010
        );
        int scaleFactor = (int) MinecraftClient.getInstance().getWindow().getScaleFactor();

        int xSize = Math.min(context.getScaledWindowWidth() - 100 / scaleFactor, 500);
        int ySize = Math.min(context.getScaledWindowHeight() - 100 / scaleFactor, 400);

        int x = (context.getScaledWindowWidth() - xSize) / 2;
        int y = (context.getScaledWindowHeight() - ySize) / 2;

        int adjScaleFactor = Math.max(2, scaleFactor);

        int openingXSize = xSize;
        int openingYSize = ySize;
        if (delta < 150) {
            openingXSize = (int) (delta * xSize / 150);
            openingYSize = 5;
        } else if (delta < 300) {
            openingYSize = 5 + (int) (delta - 150) * (ySize - 5) / 150;
        }
        RenderUtils.drawFloatingRectDark(
                context,
                (context.getScaledWindowWidth() - openingXSize) / 2,
                (context.getScaledWindowHeight() - openingYSize) / 2,
                openingXSize, openingYSize
        );
        context.enableScissor((context.getScaledWindowWidth() - openingXSize) / 2,
                (context.getScaledWindowHeight() - openingYSize) / 2,
                (context.getScaledWindowWidth() + openingXSize) / 2,
                (context.getScaledWindowHeight() + openingYSize) / 2
        );

        RenderUtils.drawFloatingRectDark(context, x + 5, y + 5, xSize - 10, 20, false);


        TextRenderUtils.drawStringCenteredScaledMaxWidth(
                processedConfig.getConfigObject().getTitle(),
                context,
                x + xSize / 2.0f,
                y + 15,
                false,
                xSize - processedConfig.getConfigObject().getSocials().size() * 18 * 2 - 25,
                0xa0a0a0
        );

        RenderUtils.drawFloatingRectDark(context, x + 4, y + 49 - 20,
                140, ySize - 54 + 20, false
        );

        int innerPadding = 20 / adjScaleFactor;
        int innerLeft = x + 4 + innerPadding;
        int innerRight = x + 144 - innerPadding;
        int innerTop = y + 49 + innerPadding;
        int innerBottom = y + ySize - 5 - innerPadding;
        context.fill(innerLeft, innerTop, innerLeft + 1, innerBottom, 0xff08080E); //Left
        context.fill(innerLeft + 1, innerTop, innerRight, innerTop + 1, 0xff08080E); //Top
        context.fill(innerRight - 1, innerTop + 1, innerRight, innerBottom, 0xff28282E); //Right
        context.fill(innerLeft + 1, innerBottom - 1, innerRight - 1, innerBottom, 0xff28282E); //Bottom
        context.fill(innerLeft + 1, innerTop + 1, innerRight - 1, innerBottom - 1, 0x6008080E); //Middle

        context.enableScissor(0, innerTop + 1, context.getScaledWindowWidth(),
                innerBottom - 1
        );

        float catBarSize = 1;
        int catY = -categoryScroll.getValue();

        LinkedHashMap<String, ProcessedCategory> currentConfigEditing = getCurrentlyVisibleCategories();
        for (Map.Entry<String, ProcessedCategory> entry : currentConfigEditing.entrySet()) {
            String selectedCategory = getSelectedCategory();
            if (selectedCategory == null || !currentConfigEditing.containsKey(selectedCategory)) {
                setSelectedCategory(entry.getKey());
            }
            String catName = entry.getValue().name;
            if (entry.getKey().equals(getSelectedCategory())) {
                catName = Formatting.DARK_AQUA.toString() + Formatting.UNDERLINE + catName;
            } else {
                catName = Formatting.GRAY + catName;
            }
            TextRenderUtils.drawStringCenteredScaledMaxWidth(catName,
                    context, x + 75, y + 70 + catY, false, 100, -1
            );
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

        context.disableScissor();

        int catDist = innerBottom - innerTop - 12;
        context.fill(innerLeft + 2, innerTop + 5, innerLeft + 7, innerBottom - 5, 0xff101010);
        context.fill(innerLeft + 3, innerTop + 6 + (int) (catDist * catBarStart), innerLeft + 6,
                innerTop + 6 + (int) (catDist * catBarEnd), 0xff303030
        );

        TextRenderUtils.drawStringCenteredScaledMaxWidth("Categories",
                context, x + 75, y + 44, false, 120, 0xa368ef
        );

        RenderUtils.drawFloatingRectDark(context, x + 149, y + 29, xSize - 154, ySize - 34, false);

        innerLeft = x + 149 + innerPadding;
        innerRight = x + xSize - 5 - innerPadding;
        innerBottom = y + ySize - 5 - innerPadding;

        context.drawTexture(GuiTextures.SEARCH, innerRight - 20, (int) (innerTop - (20 + innerPadding) / 2.0f - 9), 0, 0, 18, 18, 18, 18);

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
            int strLen = MinecraftClient.getInstance().textRenderer.getWidth(searchField.getText()) + 10;
            if (!shouldShow) strLen = 0;

            int len = Math.max(strLen, minimumSearchSize.getValue());
            searchField.setSize(len, 18);
            searchField.render(context, innerRight - 25 - len, innerTop - (20 + innerPadding) / 2 - 9);

            rightStuffLen += 5 + len;
        }

        if (getSelectedCategory() != null && currentConfigEditing.containsKey(getSelectedCategory())) {
            ProcessedCategory cat = currentConfigEditing.get(getSelectedCategory());

            TextRenderUtils.drawStringScaledMaxWidth(cat.desc,
                    context, innerLeft + 5, y + 40, true, innerRight - innerLeft - rightStuffLen - 10, 0xb0b0b0
            );
        }

        context.fill(innerLeft, innerTop, innerLeft + 1, innerBottom, 0xff08080E); //Left
        context.fill(innerLeft + 1, innerTop, innerRight, innerTop + 1, 0xff08080E); //Top
        context.fill(innerRight - 1, innerTop + 1, innerRight, innerBottom, 0xff303036); //Right
        context.fill(innerLeft + 1, innerBottom - 1, innerRight - 1, innerBottom, 0xff303036); //Bottom
        context.fill(innerLeft + 1, innerTop + 1, innerRight - 1, innerBottom - 1, 0x6008080E); //Middle

        context.enableScissor(innerLeft + 1, innerTop + 1, innerRight - 1, innerBottom - 1);
        float barSize = 1;
        int optionY = -optionsScroll.getValue();
        if (getSelectedCategory() != null && currentConfigEditing.containsKey(getSelectedCategory())) {
            ProcessedCategory cat = currentConfigEditing.get(getSelectedCategory());
            int optionWidthDefault = innerRight - innerLeft - 20;
            //GL11.glEnable(GL11.GL_DEPTH);
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
                int optionHeight = editor.getHeight();
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
            //GL11.glDisable(GL11.GL_DEPTH);
            if (optionY > 0) {
                barSize =
                        LerpUtils.clampZeroOne((float) (innerBottom - innerTop - 2) / (optionY + 5 + optionsScroll.getValue()));
            }
        }

        context.disableScissor();

        if (getSelectedCategory() != null && currentConfigEditing.containsKey(getSelectedCategory())) {
            int optionYOverlay = -optionsScroll.getValue();
            ProcessedCategory cat = currentConfigEditing.get(getSelectedCategory());
            int optionWidthDefault = innerRight - innerLeft - 20;

            context.getMatrices().translate(0, 0, 10);
            //GL11.glEnable(GL11.GL_DEPTH);
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
                int optionHeight = editor.getHeight();
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
            //GL11.glDisable(GL11.GL_DEPTH);
            context.getMatrices().translate(0, 0, -10);
        }

        optionsBarStart = optionsScroll.getValue() / (float) (optionY + optionsScroll.getValue());
        optionsBarEnd = optionsBarStart + barSize;
        if (optionsBarEnd > 1) {
            optionsBarEnd = 1;
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
        context.fill(innerRight - 10, innerTop + 5, innerRight - 5, innerBottom - 5, 0xff101010);
        context.fill(
                innerRight - 9,
                innerTop + 6 + (int) (dist * optionsBarStart),
                innerRight - 6,
                innerTop + 6 + (int) (dist * optionsBarEnd),
                0xff303030
        );

        List<Social> socials = processedConfig.getConfigObject().getSocials();
        for (int socialIndex = 0; socialIndex < socials.size(); socialIndex++) {
            Social social = socials.get(socialIndex);
            int socialLeft = x + xSize - 23 - 18 * socialIndex;
            context.drawTexture(social.getIcon(), socialLeft, y + 7, 0, 0, 16, 16, 16, 16);

            if (mouseX >= socialLeft && mouseX <= socialLeft + 16 &&
                    mouseY >= y + 6 && mouseY <= y + 23) {
                tooltipToDisplay = social.getTooltip();
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (tooltipToDisplay != null) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltipToDisplay.stream().map(Text::literal).collect(Collectors.toList()), mouseX, mouseY);
        }

        context.disableScissor();
    }

    boolean optionsBarClicked = false;
    boolean categoryBarClicked = false;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastMouseX = mouseX;

        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();

        int xSize = (int) Math.min(width - 100 / window.getScaleFactor(), 500);
        int ySize = (int) Math.min(height - 100 / window.getScaleFactor(), 400);

        int x = (window.getScaledWidth() - xSize) / 2;
        int y = (window.getScaledHeight() - ySize) / 2;

        int adjustmentFactor = (int) Math.max(2, window.getScaleFactor());
        int innerPadding = 20 / adjustmentFactor;
        int innerTop = y + 49 + innerPadding;
        int innerBottom = y + ySize - 5 - innerPadding;
        int innerLeft = x + 149 + innerPadding;
        int innerRight = x + xSize - 5 - innerPadding;

        int dist = innerBottom - innerTop - 12;
        int optionsBarStartY = (int) (innerTop + 6 + (dist * optionsBarStart));
        int optionsBarEndY = (int) (innerTop + 6 + (dist * optionsBarEnd));
        int optionsBarStartX = innerRight - 12;
        int optionsBarEndX = innerRight - 3;

        int categoryY = -categoryScroll.getValue();
        categoryY += 15 * getCurrentlyVisibleCategories().size();
        int categoryDist = innerBottom - innerTop - 12;
        float categoryBarStart = (categoryScroll.getValue() / (float) (categoryY + categoryScroll.getValue()));
        float categoryBarSize = LerpUtils.clampZeroOne((float) (innerBottom - innerTop - 2) / (categoryY + 5 + categoryScroll.getValue()));
        float categoryBarEnd = categoryBarStart + categoryBarSize;
        int categoryBarStartY = innerTop + 6 + (int) (categoryDist * categoryBarStart);
        int categoryBarEndY = innerTop + 6 + (int) (categoryDist * categoryBarEnd);
        int categoryBarStartX = x + innerPadding + 7;
        int categoryBarEndX = x + innerPadding + 12;

        if ((mouseX > optionsBarStartX && mouseX < optionsBarEndX)
                && mouseY > innerTop + 6 && mouseY < innerBottom - 6) {

            int newTarget = optionsScroll.getTarget();

            float optionBarSize = 1;
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
                    if (editor instanceof GuiOptionEditorAccordion accordion) {
                        if (accordion.getToggled()) {
                            int accordionDepth = 0;
                            if (option.accordionId >= 0) {
                                accordionDepth = activeAccordions.get(option.accordionId) + 1;
                            }
                            activeAccordions.put(accordion.getAccordionId(), accordionDepth);
                        }
                    }
                    optionY += editor.getHeight() + 5;

                    if (optionY > 0) {
                        optionBarSize = LerpUtils.clampZeroOne((float) (innerBottom - innerTop - 2) / (optionY + 5 + newTarget));
                    }
                }

                int barMax = (int) Math.floor((optionY + 5 + newTarget) - optionBarSize * (optionY + 5 + newTarget));
                if (newTarget > barMax) {
                    newTarget = barMax;
                }

                double percent = (mouseY - innerTop) / (innerBottom - innerTop);

                optionsScroll.setTimeToReachTarget(Math.min(
                        150,
                        Math.max(10, 5 * Math.abs(newTarget - optionsScroll.getValue()))
                ));
                optionsScroll.resetTimer();
                optionsScroll.setTarget((int) (barMax * percent));
            }

            // TODO clickable options bar
            return true;
        } else if ((mouseY < categoryBarStartY || mouseY > categoryBarEndY) &&
                (mouseX >= categoryBarStartX && mouseX <= categoryBarEndX) && mouseY > innerTop + 6 &&
                mouseY < innerBottom - 6) {
            // TODO clickable category bar
            return true;
        }

        if (button == 0) {
            if (getCurrentlyVisibleCategories() != null) {
                int catY = -categoryScroll.getValue();
                for (Map.Entry<String, ProcessedCategory> entry : getCurrentlyVisibleCategories().entrySet()) {
                    if (getSelectedCategory() == null) {
                        setSelectedCategory(entry.getKey());
                    }
                    if (mouseX >= x + 5 && mouseX <= x + 145 &&
                            mouseY >= y + 70 + catY - 7 && mouseY <= y + 70 + catY + 7) {
                        setSelectedCategory(entry.getKey());
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

        searchField.setFocus(mouseX >= innerRight - 20 && mouseX <= innerRight - 2 && mouseY >= (innerTop - (20 + innerPadding) / 2.0 - 9) && mouseY <= (innerTop - (20 + innerPadding) / 2.0 + 9));

        if (minimumSearchSize.getValue() > 1) {
            int stringLength = MinecraftClient.getInstance().textRenderer.getWidth(searchField.getText()) + 10;
            int length = Math.max(stringLength, minimumSearchSize.getValue());

            if (mouseX >= innerRight - 25 - length && mouseX <= innerRight - 25 &&
                    mouseY >= innerTop - (20 + innerPadding) / 2.0 - 9 && mouseY <= innerTop - (20 + innerPadding) / 2.0 + 9) {
                String oldSearch = searchField.getText();
                searchField.mouseClicked(mouseX, mouseY, button);

                if (!searchField.getText().equals(oldSearch)) updateSearchResults();
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
                        mouseY,
                        button
                ))) {
                    return true;
                }
                optionY += editor.getHeight() + 5;
            }
        }

        if (mouseX > innerLeft && mouseX < innerRight && mouseY > innerTop && mouseY < innerBottom) {
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
                            mouseY,
                            button
                    ))) {
                        return true;
                    }
                    optionY += editor.getHeight() + 5;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();

        int xSize = (int) Math.min(width - 100 / window.getScaleFactor(), 500);
        int ySize = (int) Math.min(height - 100 / window.getScaleFactor(), 400);

        int x = (window.getScaledWidth() - xSize) / 2;
        int y = (window.getScaledHeight() - ySize) / 2;

        int adjustmentFactor = (int) Math.max(2, window.getScaleFactor());
        int innerPadding = 20 / adjustmentFactor;
        int innerTop = y + 49 + innerPadding;
        int innerBottom = y + ySize - 5 - innerPadding;
        int innerLeft = x + 149 + innerPadding;
        int innerRight = x + xSize - 5 - innerPadding;


        if (mouseX > innerLeft && mouseX < innerRight && mouseY > innerTop && mouseY < innerBottom) {
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
                    if (ContextAware.wrapErrorWithContext(editor, () -> editor.mouseReleased(
                            finalX,
                            finalY,
                            finalWidth,
                            mouseX,
                            mouseY,
                            button
                    ))) {
                        return true;
                    }
                    optionY += editor.getHeight() + 5;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();

        int xSize = (int) Math.min(width - 100 / window.getScaleFactor(), 500);
        int ySize = (int) Math.min(height - 100 / window.getScaleFactor(), 400);

        int x = (window.getScaledWidth() - xSize) / 2;
        int y = (window.getScaledHeight() - ySize) / 2;

        int adjustmentFactor = (int) Math.max(2, window.getScaleFactor());
        int innerPadding = 20 / adjustmentFactor;
        int innerTop = y + 49 + innerPadding;
        int innerBottom = y + ySize - 5 - innerPadding;
        int innerLeft = x + 149 + innerPadding;
        int innerRight = x + xSize - 5 - innerPadding;


        if (mouseX > innerLeft && mouseX < innerRight && mouseY > innerTop && mouseY < innerBottom) {
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
                    if (ContextAware.wrapErrorWithContext(editor, () -> editor.mouseDragged(
                            finalX,
                            finalY,
                            finalWidth,
                            mouseX,
                            mouseY,
                            button,
                            deltaX,
                            deltaY
                    ))) {
                        return true;
                    }
                    optionY += editor.getHeight() + 5;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();

        int xSize = (int) Math.min(width - 100 / window.getScaleFactor(), 500);
        int ySize = (int) Math.min(height - 100 / window.getScaleFactor(), 400);

        int x = (window.getScaledWidth() - xSize) / 2;
        int y = (window.getScaledHeight() - ySize) / 2;

        int adjustmentFactor = (int) Math.max(2, window.getScaleFactor());
        int innerPadding = 20 / adjustmentFactor;
        int innerTop = y + 49 + innerPadding;
        int innerBottom = y + ySize - 5 - innerPadding;
        int innerLeft = x + 149 + innerPadding;

        if (mouseY > innerTop && mouseY < innerBottom) {
            if (mouseX < innerLeft) {
                int newTarget = (int) (categoryScroll.getTarget() - amount * 30);
                if (newTarget < 0) newTarget = 0;

                float tempCategoryBarSize = 1;
                int tempCategoryY = -newTarget;

                for (Map.Entry<String, ProcessedCategory> entry : getCurrentlyVisibleCategories().entrySet()) {
                    if (getSelectedCategory() == null) {
                        setSelectedCategory(entry.getKey());
                    }

                    tempCategoryY += 15;
                    if (tempCategoryY > 0) {
                        tempCategoryBarSize = LerpUtils.clampZeroOne((float) (innerBottom - innerTop - 2) / (tempCategoryY + 5 + newTarget));
                    }

                    int barMax = (int) Math.floor((tempCategoryY + 5 + newTarget) - tempCategoryBarSize * (tempCategoryY + 5 + newTarget));
                    if (newTarget > barMax) {
                        newTarget = barMax;
                    }

                    categoryScroll.resetTimer();
                    categoryScroll.setTarget(newTarget);
                }
            } else {
                int newTarget = (int) (optionsScroll.getTarget() - amount * 30);
                if (newTarget < 0) newTarget = 0;

                float optionBarSize = 1;
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
                        optionY += editor.getHeight() + 5;

                        if (optionY > 0) {
                            optionBarSize = LerpUtils.clampZeroOne((float) (innerBottom - innerTop - 2) / (optionY + 5 + newTarget));
                        }
                    }

                    int barMax = (int) Math.floor((optionY + 5 + newTarget) - optionBarSize * (optionY + 5 + newTarget));
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
            }
        }


        return super.mouseScrolled(mouseX, mouseY, amount);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int xSize = (int) Math.min(width - 100 / MinecraftClient.getInstance().getWindow().getScaleFactor(), 500);

        int adjustmentFactor = (int) Math.max(2, MinecraftClient.getInstance().getWindow().getScaleFactor());
        int innerPadding = 20 / adjustmentFactor;
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
                if (ContextAware.wrapErrorWithContext(editor, () -> editor.keyboardInput(keyCode, scanCode, modifiers))) {
                    return true;
                }
            }
        }

        if (Screen.hasControlDown() && InputUtil.GLFW_KEY_F == keyCode) {
            searchField.setFocus(!searchField.getFocus());
            return true;
        }

        if (!searchField.getFocus() && !(Character.isISOControl(keyCode)) && keyCode <= 126 && processedConfig.getConfigObject().shouldAutoFocusSearchbar()) {
            searchField.setFocus(true);
        }

        String oldSearch = searchField.getText();
        searchField.keyTyped(keyCode, scanCode, modifiers);

        if (!searchField.getText().equals(oldSearch)) {
            searchField.setText(MinecraftClient.getInstance().textRenderer.trimToWidth(
                    searchField.getText(),
                    innerWidth / 2 - 20
            ));
            updateSearchResults();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
