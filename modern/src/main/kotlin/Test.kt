import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class Test {
    init {
        object : Screen(Text.literal("")) {
            override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
                super.render(context, mouseX, mouseY, delta)
            }

            override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
                return super.mouseClicked(mouseX, mouseY, button)
            }
        }
    }
}