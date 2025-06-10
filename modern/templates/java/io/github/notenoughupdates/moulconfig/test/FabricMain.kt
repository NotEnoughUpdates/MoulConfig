package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.common.IItemStack
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.gui.CloseEventListener
import io.github.notenoughupdates.moulconfig.gui.GuiContext
import io.github.notenoughupdates.moulconfig.managed.ManagedConfig
import io.github.notenoughupdates.moulconfig.observer.ObservableList
import io.github.notenoughupdates.moulconfig.platform.MoulConfigPlatform
import io.github.notenoughupdates.moulconfig.platform.MoulConfigScreenComponent
import io.github.notenoughupdates.moulconfig.xml.Bind
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import moe.nea.shale.render.minecraft.ShaleComponent
import moe.nea.shale.util.ExampleTrees
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.io.File
import java.util.Arrays
import java.util.Random

class FabricMain : ModInitializer {
    override fun onInitialize() {
        if (System.getProperty("moulconfig.testmod") != "true") return
        val config = ManagedConfig.create(File("config/moulconfig/test.json"), TestConfig::class.java)
        ClientCommandRegistrationCallback.EVENT.register { a, b ->
            a.register(literal("moulconfig").executes {
                MinecraftClient.getInstance().send {
                    val editor = config.getEditor()
                    editor.setWide(config.instance.testCategoryA.isWide)
                    IMinecraft.INSTANCE.openWrappedScreen(editor)
                }
                0
            })
            a.register(literal("shaleuitest").executes {
                MinecraftClient.getInstance().send {
                    val tree = ExampleTrees.boxes
                    ShaleComponent(tree).openScreen()
                }
                0
            })
            a.register(literal("moulconfigxml").executes {
                MinecraftClient.getInstance().send {
                    val xmlUniverse =
                        XMLUniverse.getDefaultUniverse()
                    val scene = xmlUniverse.load(
                        ObjectBound(), MinecraftClient.getInstance().resourceManager.open(
                            Identifier.of("moulconfig:test.xml")
                        )
                    )
                    MinecraftClient.getInstance().setScreen(
                        MoulConfigScreenComponent(
                            Text.empty(),
                            GuiContext(
                                scene
                            ),
                            null
                        )
                    )
                }
                0
            })
        }
    }


    class Element(@field:Bind var text: String) {
        @field:Bind
        var enabled: Boolean = false

        @Bind
        fun randomize() {
            text = "§" + "abcdef0123456789"[Random().nextInt(16)] + text.replace("§.".toRegex(), "")
        }
    }

    class ObjectBound {
        @field:Bind
        var requestClose: Runnable? = null

        @Bind
        fun afterClose() {
            println("After close")
        }

        @Bind
        fun beforeClose(): CloseEventListener.CloseAction {
            println("Before close")
            return CloseEventListener.CloseAction.NO_OBJECTIONS_TO_CLOSE
        }

        @field:Bind
        var itemStack: IItemStack = MoulConfigPlatform.wrap(ItemStack(Blocks.SAND))

        @field:Bind
        var value: Boolean = false

        @field:Bind
        var textField: String = ""

        @field:Bind
        var slider: Float = 0f

        @Bind
        fun addElement() {
            data.add(Element(textField))
            textField = ""
        }

        @field:Bind
        var data: ObservableList<Element> =
            ObservableList(ArrayList(Arrays.asList(Element("Test 1"), Element("Test 2"), Element("Test 3"))))
    }
}
