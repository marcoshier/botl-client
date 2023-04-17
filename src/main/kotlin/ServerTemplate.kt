import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.tint
import org.openrndr.extra.noise.uniform
import org.openrndr.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(DelicateCoroutinesApi::class)
fun main() = application {
    configure {
        width = 768
        height = 576
    }

    program {

        val loader = AssetLoader()

        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).udp().bind(local)


        suspend fun send(message: String) {
            val packet = BytePacketBuilder().apply { writeText(message) }.build()
            serverSocket.send(Datagram(packet, remote))
            println("sent")
        }
        mouse.buttonUp.listen {
            launch {
                send(loader.files.random().absolutePath)
            }
        }
        extend {

        }
    }
}
