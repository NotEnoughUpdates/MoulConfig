package moe.nea.shale.util

import moe.nea.shale.dsl.box
import moe.nea.shale.dsl.buildShaleLayout
import moe.nea.shale.dsl.text
import java.awt.Color

object ExampleTrees {
    val boxes = buildShaleLayout {
        box {
            ltr()
            padding(10)
            childGap(10)
            grow()
            box {
                ttb()
                background(Color.RED)
                box {
                    background(Color.BLUE)
                    text("Goodbye, World!") {
                        color(Color.WHITE)
                    }
                }
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
                background(Color.DARK_GRAY)
                padding(10)
                text("Hello, World!") {
                    color(Color.WHITE)
                }
            }
        }
    }

}
