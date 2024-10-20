package io.github.notenoughupdates.moulconfig.internal

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDraggableList
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorText
import io.github.notenoughupdates.moulconfig.common.ClickType
import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.IKeyboardConstants
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import io.github.notenoughupdates.moulconfig.common.RenderContext
import io.github.notenoughupdates.moulconfig.gui.GuiComponentWrapper
import io.github.notenoughupdates.moulconfig.gui.GuiContext
import io.github.notenoughupdates.moulconfig.gui.GuiElement
import io.github.notenoughupdates.moulconfig.gui.GuiScreenElementWrapper
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorDraggableList
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorKeybind
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorSlider
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorText
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.event.ClickEvent
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.io.InputStream

class ForgeMinecraft : IMinecraft {
    override fun bindTexture(resourceLocation: MyResourceLocation) {
        Minecraft.getMinecraft().textureManager.bindTexture(fromMyResourceLocation(resourceLocation))
    }

    override fun loadResourceLocation(resourceLocation: MyResourceLocation): InputStream {
        return Minecraft.getMinecraft().resourceManager.getResource(fromMyResourceLocation(resourceLocation))
            .inputStream
    }

    override fun getLogger(label: String): MCLogger {
        val logger = LogManager.getLogger(label)
        return object : MCLogger {
            override fun warn(text: String) {
                logger.warn(text)
            }

            override fun info(text: String) {
                logger.info(text)
            }

            override fun error(text: String, throwable: Throwable) {
                logger.error(text, throwable)
            }

        }
    }

    override fun addExtraBuiltinConfigProcessors(processor: MoulConfigProcessor<*>) {
        processor.registerConfigEditor(
            ConfigEditorKeybind::class.java
        ) { processedOption, keybind: ConfigEditorKeybind ->
            GuiOptionEditorKeybind(
                processedOption,
                keybind.defaultKey
            )
        }
        processor.registerConfigEditor(
            ConfigEditorDraggableList::class.java
        ) { processedOption, configEditorDraggableList: ConfigEditorDraggableList ->
            GuiOptionEditorDraggableList(
                processedOption,
                configEditorDraggableList.exampleText,
                configEditorDraggableList.allowDeleting,
                configEditorDraggableList.requireNonEmpty
            )
        }
        processor.registerConfigEditor(
            ConfigEditorText::class.java
        ) { processedOption, configEditorText: ConfigEditorText? ->
            GuiOptionEditorText(
                processedOption
            )
        }
        processor.registerConfigEditor(
            ConfigEditorSlider::class.java
        ) { processedOption, configEditorSlider: ConfigEditorSlider ->
            GuiOptionEditorSlider(
                processedOption,
                configEditorSlider.minValue,
                configEditorSlider.maxValue,
                configEditorSlider.minStep
            )
        }
    }

    override val isDevelopmentEnvironment: Boolean
        get() = Launch.blackboard.get("fml.deobfuscatedEnvironment") as Boolean

    override val scaledWidth
        get(): Int = ScaledResolution(Minecraft.getMinecraft()).scaledWidth

    override val scaledHeight: Int
        get() = ScaledResolution(Minecraft.getMinecraft()).scaledHeight

    override val scaleFactor: Int
        get() = ScaledResolution(Minecraft.getMinecraft()).scaleFactor

    override fun sendClickableChatMessage(message: String, action: String, type: ClickType) {
        Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(
            ChatComponentText(message)
                .setChatStyle(
                    ChatStyle()
                        .setChatClickEvent(
                            ClickEvent(
                                when (type) {
                                    ClickType.OPEN_LINK -> ClickEvent.Action.OPEN_URL
                                    ClickType.RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND
                                }, action
                            )
                        )
                )
        )
    }

    override fun isMouseButtonDown(mouseButton: Int): Boolean {
        return Mouse.isButtonDown(mouseButton)
    }

    override fun isKeyboardKeyDown(keyboardKey: Int): Boolean {
        return Keyboard.isKeyDown(keyboardKey)
    }

    override fun provideTopLevelRenderContext(): RenderContext {
        return ForgeRenderContext()
    }

    fun openScreen(gui: GuiScreen) {
        Minecraft.getMinecraft().displayGuiScreen(gui)
    }

    override fun openWrappedScreen(gui: GuiElement) {
        openScreen(GuiScreenElementWrapper(gui))
    }

    override fun openWrappedScreen(gui: GuiContext) {
        openScreen(GuiComponentWrapper(gui))
    }

    override fun copyToClipboard(string: String) {
        try {
            Toolkit.getDefaultToolkit()
                .systemClipboard
                .setContents(StringSelection(string), null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun copyFromClipboard(): String {
        return try {
            Toolkit.getDefaultToolkit().systemClipboard.getContents(null).getTransferData(DataFlavor.stringFlavor) as String
        } catch (e: Exception) {
            null
        } ?: ""
    }

    override val mouseX: Int
        get() {
            val width = scaledWidth
            val mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth
            return mouseX
        }

    override val mouseY: Int
        get() {
            val height = scaledHeight
            val mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1
            return mouseY
        }
    override val mouseXHF: Double
        get() {
            val width = ScaledResolution(Minecraft.getMinecraft()).scaledWidth_double
            val mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth
            return mouseX
        }
    override val mouseYHF: Double
        get() {
            val height = ScaledResolution(Minecraft.getMinecraft()).scaledHeight_double
            val mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1
            return mouseY
        }

    companion object {
        @JvmStatic
        fun fromMyResourceLocation(resourceLocation: MyResourceLocation): ResourceLocation {
            return ResourceLocation(
                resourceLocation.root,
                resourceLocation.path
            )
        }

        @JvmStatic
        fun fromResourceLocation(resouceLocation: ResourceLocation): MyResourceLocation {
            return MyResourceLocation(resouceLocation.resourceDomain, resouceLocation.resourcePath)
        }
    }

    override val defaultFontRenderer: IFontRenderer
        get() = ForgeFontRenderer(Minecraft.getMinecraft().fontRendererObj)
    override val keyboardConstants: IKeyboardConstants
        get() = ForgeKeyboardConstants
}
