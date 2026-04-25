package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.common.NinePatches;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import juuxel.libninepatch.NinePatch;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class TabComponent extends GuiComponent {
    public static final class Tab {
        private final GuiComponent header;
        private final GuiComponent body;

        public Tab(GuiComponent header, GuiComponent body) {
            this.header = header;
            this.body = body;
        }

        public GuiComponent getHeader() {
            return header;
        }

        public GuiComponent getBody() {
            return body;
        }

        public GuiComponent component1() { return header; }
        public GuiComponent component2() { return body; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Tab)) return false;
            Tab tab = (Tab) o;
            return Objects.equals(header, tab.header) && Objects.equals(body, tab.body);
        }

        @Override
        public int hashCode() {
            return Objects.hash(header, body);
        }

        @Override
        public String toString() {
            return "Tab(header=" + header + ", body=" + body + ")";
        }
    }

    private final List<Tab> tabs;
    private final GetSetter<Integer> selectedTabIndex;
    public final NinePatch<MyResourceLocation> panelStyle = NinePatches.createVanillaPanel();
    public final NinePatch<MyResourceLocation> tabSelectedHeaderBackground = NinePatch.builder(GuiTextures.VANILLA_TAB_SELECTED)
        .cornerSize(4)
        .cornerUv(4 / 16F)
        .mode(NinePatch.Mode.STRETCHING)
        .build();
    public final NinePatch<MyResourceLocation> tabUnselectedHeaderBackground = NinePatch.builder(GuiTextures.VANILLA_TAB_UNSELECTED)
        .cornerSize(4)
        .cornerUv(4 / 16F)
        .mode(NinePatch.Mode.STRETCHING)
        .build();

    public final int headerPadding = 4;
    public final int headerSpacing = 4;
    public final int bodyPadding = 4;
    public final int initialHeaderOffset = bodyPadding + 4;
    public final int headerInset = 4;

    public TabComponent(List<Tab> tabs, GetSetter<Integer> selectedTabIndex) {
        if (tabs.isEmpty()) {
            throw new IllegalArgumentException("tabs must not be empty");
        }
        this.tabs = tabs;
        this.selectedTabIndex = selectedTabIndex;
    }

    public List<Tab> getTabs() {
        return tabs;
    }

    public GetSetter<Integer> getSelectedTabIndex() {
        return selectedTabIndex;
    }

    @Override
    public int getWidth() {
        int maxBodyWidth = 0;
        int totalHeaderWidth = initialHeaderOffset;
        for (Tab tab : tabs) {
            maxBodyWidth = Math.max(maxBodyWidth, tab.body.getWidth());
            totalHeaderWidth += tab.header.getWidth() + headerSpacing + headerPadding * 2;
        }
        return Math.max(maxBodyWidth + 2 * bodyPadding, totalHeaderWidth);
    }

    @Override
    public int getHeight() {
        int maxBodyHeight = 0;
        int maxHeaderHeight = 0;
        for (Tab tab : tabs) {
            maxBodyHeight = Math.max(maxBodyHeight, tab.body.getHeight());
            maxHeaderHeight = Math.max(maxHeaderHeight, tab.header.getHeight());
        }
        return maxBodyHeight + maxHeaderHeight + bodyPadding * 2 + headerPadding;
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        T acc = initial;
        for (Tab tab : tabs) {
            acc = visitor.apply(tab.header, visitor.apply(tab.body, acc));
        }
        return acc;
    }

    @Override
    public void render(GuiImmediateContext context) {
        int headerHeight = maxHeaderHeight();
        context.getRenderContext().drawNinePatch(panelStyle, 0F, (float) (headerHeight + headerPadding), context.getWidth(), context.getHeight() - headerHeight - headerPadding);

        Tab selectedTab = null;
        int headerOffset = initialHeaderOffset;
        for (int index = 0; index < tabs.size(); index++) {
            Tab tab = tabs.get(index);
            boolean selected = index == selectedTabIndex.get();
            NinePatch<MyResourceLocation> background;
            if (selected) {
                selectedTab = tab;
                background = tabSelectedHeaderBackground;
            } else {
                background = tabUnselectedHeaderBackground;
            }
            context.getRenderContext().drawNinePatch(background, (float) headerOffset, 0F, tab.header.getWidth() + 2 * headerPadding, headerHeight + headerPadding + headerInset);
            GuiImmediateContext child = context.translated(headerOffset + headerPadding, headerPadding, tab.header.getWidth(), headerHeight);
            context.getRenderContext().pushMatrix();
            context.getRenderContext().translate((float) (headerOffset + headerPadding), (float) headerPadding);
            tab.header.render(child);
            context.getRenderContext().popMatrix();
            headerOffset += tab.header.getWidth() + 2 * headerPadding + headerSpacing;
        }

        if (selectedTab == null) {
            return;
        }

        GuiImmediateContext child = context.translated(bodyPadding, headerHeight + bodyPadding, context.getWidth() - bodyPadding * 2, context.getHeight() - headerHeight - bodyPadding);
        context.getRenderContext().pushMatrix();
        context.getRenderContext().translate((float) bodyPadding, (float) (headerHeight + bodyPadding));
        selectedTab.body.render(child);
        context.getRenderContext().popMatrix();
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        int headerHeight = maxHeaderHeight();
        Tab selectedTab = null;
        int headerOffset = initialHeaderOffset;
        for (int index = 0; index < tabs.size(); index++) {
            Tab tab = tabs.get(index);
            if (index == selectedTabIndex.get()) {
                selectedTab = tab;
            }
            GuiImmediateContext child = context.translated(headerOffset + headerPadding, headerPadding, tab.header.getWidth(), headerHeight);
            if (child.isHovered() && mouseEvent instanceof MouseEvent.Click && ((MouseEvent.Click) mouseEvent).getMouseState()) {
                if (selectedTabIndex.get() != index) {
                    getContext().setFocusedElement(null);
                }
                selectedTabIndex.set(index);
                return true;
            }
            headerOffset += tab.header.getWidth() + 2 * headerPadding + headerSpacing;
        }

        if (selectedTab == null) return false;
        GuiImmediateContext child = context.translated(bodyPadding, headerHeight + bodyPadding, context.getWidth() - bodyPadding * 2, context.getHeight() - headerHeight - bodyPadding);
        return selectedTab.body.mouseEvent(mouseEvent, child);
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        int headerHeight = maxHeaderHeight();
        Tab selectedTab = null;
        for (int index = 0; index < tabs.size(); index++) {
            if (index == selectedTabIndex.get()) {
                selectedTab = tabs.get(index);
            }
        }
        if (selectedTab == null) return false;
        GuiImmediateContext child = context.translated(bodyPadding, headerHeight + bodyPadding, context.getWidth() - bodyPadding * 2, context.getHeight() - headerHeight - bodyPadding);
        return selectedTab.body.keyboardEvent(event, child);
    }

    private int maxHeaderHeight() {
        int headerHeight = 0;
        for (Tab tab : tabs) {
            headerHeight = Math.max(headerHeight, tab.header.getHeight());
        }
        return headerHeight;
    }
}
