import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.loadFont
import org.openrndr.draw.renderTarget
import org.openrndr.extra.imageFit.imageFit
import org.openrndr.launch
import org.openrndr.math.Vector2
import kotlin.math.sin

@OptIn(DelicateCoroutinesApi::class)
fun main() = application {
    configure {
        width = 1280
        height = 720
    }
    program {

        val rt = renderTarget(width, height) {
            colorBuffer()
            depthBuffer()
        }

        val loader = AssetLoader().apply { load() }

        loader.source0?.newFrame?.listen {
            drawer.isolatedWithTarget(rt) {
                println("0")
                drawer.clear(ColorRGBa.BLACK)
                drawer.imageFit(it.frame, drawer.bounds)
            }
        }

        loader.source1?.newFrame?.listen {
            drawer.isolatedWithTarget(rt) {
                println("1")
                drawer.clear(ColorRGBa.BLACK)
                drawer.imageFit(it.frame, drawer.bounds)
                drawer.circle(drawer.bounds.center, 100.0 * sin(seconds) + 100.0)
            }
        }

        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).udp().connect(local, remote)

        launch {
            while (true) {
                val msg = socket.incoming.receive()
                val text = msg.packet.readText()
                loader.currentPath = text
            }
        }
        extend {
            loader.source0?.draw(drawer, false)
            loader.source1?.draw(drawer, false)

            //drawer.image(rt.colorBuffer(0))
        }
    }
}