import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.loadImage
import org.openrndr.ffmpeg.PlayMode
import org.openrndr.ffmpeg.VideoPlayerFFMPEG
import java.io.File

class AssetLoader {

    val path = "/home/marco/Desktop/Archive_0.1/Archive_segmented/Videos/"
    val files = File(path).walk().filter { it.isFile }.toMutableList()

    val videos = mutableListOf<VideoPlayerFFMPEG>()
    val images = mutableListOf<ColorBuffer>()

    var source0: VideoPlayerFFMPEG? = null
        set(value) {
            field = value
            value?.play()
        }
    var source1: VideoPlayerFFMPEG? = null
        set(value) {
            field = value
            value?.play()
        }

    var toggle = true
    var currentPath = ""
        set(value) {
            field = value
            toggle = !toggle
            println(value)
            if(toggle) {
                source0?.apply {
                    pause()
                    dispose()
                }
                source0 = null
                source1 = VideoPlayerFFMPEG.fromFile(value, PlayMode.VIDEO)
            } else {
                source1?.apply {
                    pause()
                    dispose()
                }
                source1 = null
                source0 = VideoPlayerFFMPEG.fromFile(value, PlayMode.VIDEO)
            }
        }

    fun load() {
        files.forEachIndexed { i, it ->
            println("$i/${files.size}")
            when(it.extension) {
                "mp4" -> videos.add(VideoPlayerFFMPEG.fromFile(it.path))
                "jpg", "png" -> loadImage(it)
            }
        }
    }
}