package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.common.IItemStack
import io.github.notenoughupdates.moulconfig.gui.GuiComponentWrapper
import io.github.notenoughupdates.moulconfig.gui.GuiContext
import io.github.notenoughupdates.moulconfig.observer.ObservableList
import io.github.notenoughupdates.moulconfig.platform.ModernItemStack
import io.github.notenoughupdates.moulconfig.xml.Bind
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

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

    class ObjectBound {
        @field:Bind("sliderLol")
        var slider: Float = 0F

        @field:Bind
        var data: ObservableList<IItemStack> =
            ObservableList(mutableListOf())

        @field:Bind
        var search: String = ""

        @Bind
        fun click() {
            data.add(ModernItemStack.of(ItemStack(Registries.ITEM.entrySet.random().value)))
        }
    }
}