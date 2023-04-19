import org.openrndr.application
import org.openrndr.events.Event
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.net.*
import kotlin.concurrent.thread

val port = 9002

class Receiver {
    val stateReceived = Event<String>()
    val quit = false

    val networkInterface = NetworkInterface.getNetworkInterfaces().toList().find {
        !it.isVirtual && it.isUp && !it.isVirtual && it.supportsMulticast() && it.interfaceAddresses.find {
            it.address.hostAddress.contains("192.168.42.")
            //true
        }!= null }

    fun work() {

        val serverSocket: DatagramSocket
        try {
            val addr = networkInterface!!.inetAddresses.nextElement()
            serverSocket = DatagramSocket(InetSocketAddress(addr, port))

        } catch (e1: SocketException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
            return
        }

        val receiveData = ByteArray(65536)

        while (!quit) {
            val receivePacket = DatagramPacket(receiveData, receiveData.size)
            try {
                serverSocket.receive(receivePacket)
                val `is` = ByteArrayInputStream(receiveData)
                val ois = ObjectInputStream(`is`)
                val `object` = ois.readObject()

                if (`object` is String) {
                    stateReceived.trigger((`object`))
                }
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
    }

}

fun main() = application {
    program {

        val receiver = Receiver()

        receiver.stateReceived.listen {
            println(it)
        }

        thread {
            while (true) {
               receiver.work()
            }
        }
        extend {


        }
    }
}