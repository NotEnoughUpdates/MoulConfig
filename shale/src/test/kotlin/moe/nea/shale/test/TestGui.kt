package moe.nea.shale.test

import moe.nea.shale.layout.Size
import moe.nea.shale.layout.Sizing
import moe.nea.shale.render.awt.AwtGraphicsContext
import moe.nea.shale.util.ExampleTrees
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

class TestGui : JPanel() {
    val tree = ExampleTrees.boxes
    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        tree.sizing = Sizing.Fixed(Size.coerced(width, height))
        val ctx = AwtGraphicsContext(g!!)
        tree.relayout(ctx)
        tree.render(ctx)
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val gui = JFrame("TestGui")
        gui.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        gui.preferredSize = Dimension(800, 600)
        gui.isVisible = true
        gui.layout = BorderLayout()
        gui.add(TestGui(), BorderLayout.CENTER)
    }
}
