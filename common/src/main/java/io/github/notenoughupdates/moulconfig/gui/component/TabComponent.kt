package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.GuiTextures
import io.github.notenoughupdates.moulconfig.common.NinePatches
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import juuxel.libninepatch.NinePatch
import java.util.function.BiFunction

data class TabComponent(
    val tabs: List<Tab>,
    val selectedTabIndex: GetSetter<Int>
) : GuiComponent() {
    data class Tab(
        val header: GuiComponent,
        val body: GuiComponent,
    )

    val panelStyle = NinePatches.createVanillaPanel()

    val tabSelectedHeaderBackground = NinePatch.builder(GuiTextures.VANILLA_TAB_SELECTED)
        .cornerSize(4)
        .cornerUv(4 / 16F)
        .mode(NinePatch.Mode.STRETCHING)
        .build()
    val tabUnselectedHeaderBackground = NinePatch.builder(GuiTextures.VANILLA_TAB_UNSELECTED)
        .cornerSize(4)
        .cornerUv(4 / 16F)
        .mode(NinePatch.Mode.STRETCHING)
        .build()

    val headerPadding = 4 // Top left and right padding of the tab headers
    val headerSpacing = 4 // Spacing between two header tabs
    val bodyPadding = 4 // Bottom left and right padding of the main content panel
    val initialHeaderOffset = bodyPadding + 4 // Spacing from the left to the first header tab
    val headerInset = 4 // How far the tab header should be inset and override the panel

    init {
        require(tabs.isNotEmpty())
    }

    override fun getWidth(): Int {
        return maxOf(tabs.maxOf { it.body.width } + 2 * bodyPadding,
                     initialHeaderOffset + tabs.sumOf { it.header.width + headerSpacing + headerPadding * 2 })
    }

    override fun getHeight(): Int {
        return tabs.maxOf { it.body.height } + tabs.maxOf { it.header.height } + bodyPadding * 2 + headerPadding
    }

    override fun <T> foldChildren(initial: T, visitor: BiFunction<GuiComponent, T, T>): T {
        return tabs.fold(initial) { acc, tab -> visitor.apply(tab.header, visitor.apply(tab.body, acc)) }
    }

    override fun render(context: GuiImmediateContext) {
        val headerHeight = tabs.maxOf { it.header.height }

        context.renderContext.drawNinePatch(
            panelStyle,
            0.toFloat(), headerHeight.toFloat() + headerPadding,
            context.width, context.height - headerHeight - headerPadding
        )

        var selectedTab: Tab? = null
        var headerOffset = initialHeaderOffset
        for ((index, tab) in tabs.withIndex()) {
            val isSelected = index == selectedTabIndex.get()
            val background = if (isSelected) {
                selectedTab = tab
                tabSelectedHeaderBackground
            } else {
                tabUnselectedHeaderBackground
            }
            context.renderContext.drawNinePatch(
                background,
                headerOffset.toFloat(), 0f,
                tab.header.width + 2 * headerPadding, headerHeight + headerPadding + headerInset
            )
            val child = context.translated(
                headerOffset + headerPadding, headerPadding,
                tab.header.width, headerHeight)
            context.renderContext.pushMatrix()
            context.renderContext.translate(headerOffset + headerPadding + 0F, headerPadding + 0F, 0F)
            tab.header.render(child)
            context.renderContext.popMatrix()
            headerOffset += tab.header.width + 2 * headerPadding + headerSpacing
        }

        selectedTab ?: return

        val child = context.translated(bodyPadding, headerHeight + bodyPadding, context.width - bodyPadding * 2, context.height - headerHeight - bodyPadding)
        context.renderContext.pushMatrix()
        context.renderContext.translate(bodyPadding + 0F, headerHeight + bodyPadding + 0F, 0F)
        selectedTab.body.render(child)
        context.renderContext.popMatrix()
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        val headerHeight = tabs.maxOf { it.header.height }

        var selectedTab: Tab? = null
        var headerOffset = initialHeaderOffset
        for ((index, tab) in tabs.withIndex()) {
            val isSelected = index == selectedTabIndex.get()
            if (isSelected) {
                selectedTab = tab
            }
            val child = context.translated(
                headerOffset + headerPadding, headerPadding,
                tab.header.width, headerHeight)
            if (child.isHovered && mouseEvent is MouseEvent.Click && mouseEvent.mouseState) {
                if (selectedTabIndex.get() != index) {
                    this.context.setFocusedElement(null)
                }
                selectedTabIndex.set(index)
                return true
            }
            headerOffset += tab.header.width + 2 * headerPadding + headerSpacing
        }

        selectedTab ?: return false

        val child = context.translated(bodyPadding, headerHeight + bodyPadding, context.width - bodyPadding * 2, context.height - headerHeight - bodyPadding)
        return selectedTab.body.mouseEvent(mouseEvent, child)
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        val headerHeight = tabs.maxOf { it.header.height }

        var selectedTab: Tab? = null
        for ((index, tab) in tabs.withIndex()) {
            val isSelected = index == selectedTabIndex.get()
            if (isSelected) {
                selectedTab = tab
            }
        }

        selectedTab ?: return false

        val child = context.translated(bodyPadding, headerHeight + bodyPadding, context.width - bodyPadding * 2, context.height - headerHeight - bodyPadding)
        return selectedTab.body.keyboardEvent(event, child)
    }
}
