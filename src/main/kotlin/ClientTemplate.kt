import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.openrndr.application
import org.openrndr.draw.loadFont
import org.openrndr.launch
import org.openrndr.math.Vector2
import kotlin.math.sin

val local = InetSocketAddress("127.0.0.1", 9002)
val remote = InetSocketAddress("127.0.0.1", 9003)

@OptIn(DelicateCoroutinesApi::class)
fun main() = application {
    configure { }
    program {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).udp().connect(local, remote)

        var text = ""

        GlobalScope.launch {
            while (true) {
                val msg = socket.incoming.receive()
                println(msg)
                text = msg.packet.readText()
            }
        }

        val font = loadFont("data/fonts/default.otf", 18.0)
        extend {
            drawer.fontMap = font
            val w = text.fold(0.0) { a, b -> a + font.characterWidth(b) }
            drawer.translate( Vector2(- w / 2.0, - 9.0) + drawer.bounds.center)
            drawer.text(text)

        }
    }
}