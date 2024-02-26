package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.common.IItemStack
import io.github.notenoughupdates.moulconfig.gui.CloseEventListener
import io.github.notenoughupdates.moulconfig.gui.GuiComponentWrapper
import io.github.notenoughupdates.moulconfig.gui.GuiContext
import io.github.notenoughupdates.moulconfig.observer.ObservableList
import io.github.notenoughupdates.moulconfig.platform.ModernItemStack
import io.github.notenoughupdates.moulconfig.xml.Bind
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import java.util.*

class FabricMain : ModInitializer {
    override fun onInitialize() {
        ClientCommandRegistrationCallback.EVENT.register { a, b ->
            a.register(literal("moulconfig").executes {
                MinecraftClient.getInstance().send {
                    val xmlUniverse =
                        XMLUniverse.getDefaultUniverse()
                    val scene = xmlUniverse.load(
                        ObjectBound(), MinecraftClient.getInstance().resourceManager.open(
                            Identifier("moulconfig:test.xml")
                        )
                    )
                    MinecraftClient.getInstance().setScreen(
                        GuiComponentWrapper(
                            GuiContext(
                                scene
                            )
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
        var itemStack: IItemStack = ModernItemStack.of(ItemStack(Blocks.SAND))

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