import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.imageFit.imageFit
import org.openrndr.launch
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import kotlin.concurrent.thread
import kotlin.math.sin

val test = false
@OptIn(DelicateCoroutinesApi::class)
fun main() = application {
    configure {
        if(test) {
            width = 1280
            height = 720
        } else {

            width = 1280
            height = 720
            fullscreen = Fullscreen.SET_DISPLAY_MODE

        }
    }
    program {


        val loader = AssetLoader().apply { load() }

        val receiver = Receiver()

        receiver.stateReceived.listen {
            if(it == "START") {
                loader.start = true
                loader.play()
            } else {
                loader.currentLabels.clear()
                loader.imagePaths.clear()
                val imagesToLabels = (it.split(",")).map { f ->
                    val split = f.split("|")
                    val image = split[0].substring(split[0].indexOf("Archive_images"))
                    val label = split[1]
                    image to label
                }
                imagesToLabels.forEach { (img, lab) ->
                    loader.imagePaths.add(img)
                    loader.currentLabels.add(lab)
                }
            }
        }

        thread {
            while (true) {
                receiver.work()
            }
        }
        val statusbar = Rectangle(20.0, height - 40.0, width - 40.0, 20.0)
        extend {
            loader.updateAnimation()

            if(loader.start) {
                loader.update()


                drawer.imageFit(loader.currentImages[loader.oldT], drawer.bounds)
                drawer.stroke = null
                drawer.fill = ColorRGBa.WHITE
                drawer.rectangle(statusbar)

                val offset = statusbar.offsetEdges(-2.0)
                for((index, image) in loader.currentImages.withIndex()) {
                    drawer.image(image, image.bounds, offset.sub((1.0 / loader.currentImages.size) * index, 0.0, (1.0 / loader.currentImages.size) * (index + 1), 1.0))
                }
                drawer.fill = ColorRGBa.RED
                drawer.circle(statusbar.x + (statusbar.width * loader.timer), statusbar.y + (statusbar.height / 2.0), 9.0)

                for((index, label) in loader.currentLabels.take(loader.oldT + 1).withIndex()) {
                    drawer.text(label, 20.0, 30.0 + 12.0 * index)
                }
            }

        }
    }
}