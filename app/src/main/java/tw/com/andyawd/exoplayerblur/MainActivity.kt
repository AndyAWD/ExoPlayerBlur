package tw.com.andyawd.exoplayerblur

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
import com.google.android.exoplayer2.util.Util
import com.zhouwei.blurlibrary.EasyBlur
import kotlinx.android.synthetic.main.activity_main.*
import tw.com.andyawd.andyawdlibrary.AWDLog
import java.io.IOException

class MainActivity : AppCompatActivity(), LoadErrorHandlingPolicy, Player.EventListener,
    TextureView.SurfaceTextureListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pvAmVideo.visibility = View.VISIBLE

        tvAmVideo.surfaceTextureListener = this
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_ENDED -> {
                AWDLog.d("影片結束")
            }
            ExoPlayer.STATE_READY -> {
                AWDLog.d("影片準備中")
            }
            ExoPlayer.STATE_BUFFERING -> {
                AWDLog.d("影片緩充中")
            }
            ExoPlayer.STATE_IDLE -> {
                AWDLog.d("影片閒置")
            }
            else -> {
                return
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        return
    }

    override fun getRetryDelayMsFor(
        dataType: Int,
        loadDurationMs: Long,
        exception: IOException?,
        errorCount: Int
    ): Long {
        return 0
    }

    override fun getMinimumLoadableRetryCount(dataType: Int): Int {
        return 0
    }

    override fun getBlacklistDurationMsFor(
        dataType: Int,
        loadDurationMs: Long,
        exception: IOException?,
        errorCount: Int
    ): Long {
        return 0
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        return
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        val bitmapBlur =
            EasyBlur.with(EBApplication.context()).bitmap(tvAmVideo.bitmap).scale(10).radius(25)
                .blur()
        ivAmVideoBlur.setImageBitmap(bitmapBlur)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl: DefaultLoadControl = DefaultLoadControl()
        val simpleExoPlayer =
            ExoPlayerFactory.newSimpleInstance(EBApplication.context(), trackSelector, loadControl)
        val defaultBandwidthMeter: DefaultBandwidthMeter =
            DefaultBandwidthMeter.Builder(EBApplication.context()).build()
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            EBApplication.context(),
            Util.getUserAgent(EBApplication.context(), resources.getString(R.string.app_name)),
            defaultBandwidthMeter
        )
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
        val progressiveMediaSource: ProgressiveMediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
                .setLoadErrorHandlingPolicy(this)
                .createMediaSource(Constants.VIDEO_URL.toUri())

        simpleExoPlayer.prepare(progressiveMediaSource)
        simpleExoPlayer.setVideoSurface(Surface(surface))
        simpleExoPlayer.addListener(this)
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer?.repeatMode = Player.REPEAT_MODE_ALL
    }
}

