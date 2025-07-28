package io.github.notenoughupdates.moulconfig.internal

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorText
import io.github.notenoughupdates.moulconfig.common.*
import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import io.github.notenoughupdates.moulconfig.gui.GuiComponentWrapper
import io.github.notenoughupdates.moulconfig.gui.GuiContext
import io.github.notenoughupdates.moulconfig.gui.GuiElement
import io.github.notenoughupdates.moulconfig.gui.GuiScreenElementWrapper
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorKeybindL
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorSliderL
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorTextL
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.event.ClickEvent
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.util.IChatComponent
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.image.BufferedImage
import java.io.InputStream

class ForgeMinecraft : IMinecraft {
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
            GuiOptionEditorKeybindL(
                processedOption,
                keybind.defaultKey
            )
        }
        processor.registerConfigEditor(
            ConfigEditorText::class.java
        ) { processedOption, configEditorText: ConfigEditorText? ->
            GuiOptionEditorTextL(
                processedOption
            )
        }
        processor.registerConfigEditor(
            ConfigEditorSlider::class.java
        ) { processedOption, configEditorSlider: ConfigEditorSlider ->
            GuiOptionEditorSliderL(
                processedOption,
                configEditorSlider.minValue,
                configEditorSlider.maxValue,
                configEditorSlider.minStep
            )
        }
    }

    override fun isDevelopmentEnvironment(): Boolean {
        return Launch.blackboard.get("fml.deobfuscatedEnvironment") as Boolean
    }

    override fun getScaledWidth(): Int {
        return ScaledResolution(Minecraft.getMinecraft()).scaledWidth
    }

    override fun getScaledHeight(): Int {
        return ScaledResolution(Minecraft.getMinecraft()).scaledHeight
    }

    override fun getScaleFactor(): Int {
        return ScaledResolution(Minecraft.getMinecraft()).scaleFactor
    }

    override fun sendClickableChatMessage(message: StructuredText, action: String, type: ClickType?) {
        val component = StructuredTextImpl.unwrap(message)
        if (type != null)
            component.chatStyle = component.chatStyle
                .setChatClickEvent(
                    ClickEvent(
                        when (type) {
                            ClickType.OPEN_LINK -> ClickEvent.Action.OPEN_URL
                            ClickType.RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND
                        }, action
                    )
                )
        Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(
            component

        )
    }

    override fun generateDynamicTexture(image: BufferedImage): DynamicTextureReference {
        val texture = DynamicTexture(image)
        val res = Minecraft.getMinecraft().textureManager.getDynamicTextureLocation("moulconfigdyn", texture)

        return object : DynamicTextureReference() {
            override val identifier: MyResourceLocation
                get() = fromResourceLocation(res)

            override fun update(bufferedImage: BufferedImage) {
                bufferedImage.getRGB(
                    0, 0, bufferedImage.width, bufferedImage.height,
                    texture.textureData, 0, bufferedImage.width
                )
                texture.updateDynamicTexture()
            }

            override fun doDestroy() {
                Minecraft.getMinecraft().textureManager.deleteTexture(res)
            }
        }
    }

    override fun isGeneratedSentinel(resourceLocation: MyResourceLocation): Boolean {
        return resourceLocation.root == "moulconfigdyn" // technically this will also start with dynamic/ but i dont control that, so i will just use another namespace smilers
    }

    override fun getKeyName(keyCode: Int): StructuredText {
        return StructuredText.of(KeybindHelper.getKeyName(keyCode))
    }

    override fun createLiteral(text: String): StructuredText {
        return StructuredTextImpl.wrap(ChatComponentText(text))
    }

    override fun createTranslatable(key: String, vararg args: StructuredText): StructuredText {
        return StructuredTextImpl.wrap(ChatComponentTranslation(key, *args))
    }

    override fun createStructuredTextInternal(obj: Any): StructuredText? {
        if (obj is IChatComponent)
            return StructuredTextImpl.wrap(obj)
        return null
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

    override fun getMousePositionHF(): Pair<Double, Double> {
        val sr = ScaledResolution(Minecraft.getMinecraft())
        val width = sr.scaledWidth_double
        val mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth
        val height = sr.scaledHeight_double
        val mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1
        return mouseX to mouseY
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

    override fun isOnMacOs(): Boolean {
        return Minecraft.isRunningOnMac
    }

    override fun getDefaultFontRenderer(): IFontRenderer {
        return ForgeFontRenderer(Minecraft.getMinecraft().fontRendererObj)
    }

    override fun getKeyboardConstants(): IKeyboardConstants {
        return ForgeKeyboardConstants
    }
}
