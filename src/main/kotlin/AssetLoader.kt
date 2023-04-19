import org.openrndr.animatable.Animatable
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.loadImage
import org.openrndr.ffmpeg.PlayMode
import org.openrndr.ffmpeg.VideoPlayerFFMPEG
import java.io.File

class AssetLoader: Animatable() {

    var start = false
    var timer = 0.0

    val path = "C:/Users/RNDR/Desktop/Archive_0.1/Archive_images/"
    var files = File(path).walk().filter { it.isFile }.toMutableList()

    var videos = mutableListOf<VideoPlayerFFMPEG>()
    val images = mutableListOf<ColorBuffer>()
    var imagePaths = MutableList(12) { files.random().path }
        set(value) {
            field = value
            images.clear()
            for(p in field) {
                images.add(loadImage(p))
            }
        }

    var currentImages = mutableListOf<ColorBuffer>()
    var currentLabels = mutableListOf<String>()

    var source0: VideoPlayerFFMPEG? = null
        set(value) {
            field = value
            //value?.play()
        }

    fun load() {

        files.forEachIndexed { i, it ->
            println("$i/${files.size}")
            when(it.extension) {
                "mp4" -> videos.add(VideoPlayerFFMPEG.fromFile(it.path, PlayMode.VIDEO))
                "jpg", "png" -> images.add(loadImage(it))
            }
        }
        currentImages = MutableList(12) { images.random() }
        currentLabels = MutableList(12) { "A" }
    }

    fun play() {
        ::timer.animate(1.0, 60000).completed.listen {
            start = false
        }
    }

    var oldT = 0
    fun update() {
        val t = (timer * currentImages.size).toInt().coerceIn(currentImages.indices)
        if(oldT != t) {
            oldT = t
        }
    }

}