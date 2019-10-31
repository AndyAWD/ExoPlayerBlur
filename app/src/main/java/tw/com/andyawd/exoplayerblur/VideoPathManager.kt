package tw.com.andyawd.exoplayerblur

import android.net.Uri
import androidx.core.net.toUri
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource

class VideoPathManager {
    companion object {
        val instance = VideoPathHolder.videoPathHolder
    }

    private object VideoPathHolder {
        val videoPathHolder = VideoPathManager()
    }

    fun exoPlayerRaw(path: Int): Uri {
        val dataSpec = DataSpec(RawResourceDataSource.buildRawResourceUri(path))
        val rawResourceDataSource = RawResourceDataSource(EBApplication.context())
        rawResourceDataSource.open(dataSpec)
        return rawResourceDataSource.uri!!
    }

    fun videoViewRaw(path: Int): Uri =
        Uri.parse("android.resource://${EBApplication.context().packageName}/${path}")

    fun url(path: String): Uri = path.toUri()
}