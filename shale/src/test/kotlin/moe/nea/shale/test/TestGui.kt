package moe.nea.shale.test

import moe.nea.shale.dsl.box
import moe.nea.shale.dsl.buildShaleLayout
import moe.nea.shale.layout.Size
import moe.nea.shale.layout.Sizing
import moe.nea.shale.render.AwtGraphicsContext
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

class TestGui : JPanel() {
    val tree = buildShaleLayout {
        box {
            rtl()
            padding(10)
            childGap(10)
            grow()
            box {
                background(Color.RED)
                grow()
            }
            box {
                background(Color.GREEN)
                grow()
            }
            box {
                background(Color.CYAN)
                fixed(200, 100)
            }
            box {
                background(Color.BLUE)
                fixed(20, 1000)
            }
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        tree.sizing = Sizing.Fixed(Size.coerced(width, height))
        tree.relayout()
        tree.render(AwtGraphicsContext(g!!))
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
