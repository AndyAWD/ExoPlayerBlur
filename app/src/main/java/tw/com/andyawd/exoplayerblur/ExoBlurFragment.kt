package tw.com.andyawd.exoplayerblur

import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
import com.google.android.exoplayer2.util.Util
import com.zhouwei.blurlibrary.EasyBlur
import java.io.IOException

class ExoBlurFragment : Fragment(), LoadErrorHandlingPolicy, Player.EventListener,
    TextureView.SurfaceTextureListener {

    private var pvFebVideo: PlayerView? = null
    private var tvFebVideo: TextureView? = null
    private var ivFebVideoBlur: ImageView? = null

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exo_blur, container, false)

        initComponent(view)
        initListener()

        return view
    }

    override fun onStart() {
        super.onStart()

        initExoPlayer()

        if (tvFebVideo?.isAvailable!!) {
            onSurfaceTextureAvailable(
                tvFebVideo?.surfaceTexture,
                tvFebVideo?.width!!.toInt(),
                tvFebVideo?.width!!.toInt()
            )
        }

    }

    override fun onResume() {
        super.onResume()

        simpleExoPlayer?.playWhenReady = isPlaying
    }

    override fun onPause() {
        super.onPause()

        simpleExoPlayer?.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()

        simpleExoPlayer?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()

        simpleExoPlayer?.release()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_ENDED -> {
                return
            }
            ExoPlayer.STATE_READY -> {
                return
            }
            ExoPlayer.STATE_BUFFERING -> {
                return
            }
            ExoPlayer.STATE_IDLE -> {
                return
            }
            else -> {
                return
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
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
            EasyBlur.with(EBApplication.context()).bitmap(tvFebVideo?.bitmap).scale(10).radius(25)
                .blur()
        ivFebVideoBlur?.setImageBitmap(bitmapBlur)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        simpleExoPlayer?.setVideoSurface(Surface(surface))
    }

    private fun initComponent(view: View) {
        pvFebVideo = view.findViewById(R.id.pvFebVideo)
        tvFebVideo = view.findViewById(R.id.tvFebVideo)
        ivFebVideoBlur = view.findViewById(R.id.ivFebVideoBlur)
    }

    private fun initListener() {
        tvFebVideo?.surfaceTextureListener = this
    }

    private fun initExoPlayer() {
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl()
        simpleExoPlayer =
            ExoPlayerFactory.newSimpleInstance(EBApplication.context(), trackSelector, loadControl)

        simpleExoPlayer?.prepare(getMediaSource(Constants.VIDEO_URL.toUri()))
        simpleExoPlayer?.addListener(this)
        simpleExoPlayer?.playWhenReady = true
        simpleExoPlayer?.repeatMode = Player.REPEAT_MODE_ALL

        tvFebVideo?.alpha = 0.0f    //把TextureView設為透明，這樣模糊失敗的話也看不到影片
        isPlaying = simpleExoPlayer?.playWhenReady!!
    }

    private fun getMediaSource(uri: Uri): ProgressiveMediaSource {
        val defaultBandwidthMeter: DefaultBandwidthMeter =
            DefaultBandwidthMeter.Builder(EBApplication.context()).build()
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            EBApplication.context(),
            Util.getUserAgent(EBApplication.context(), resources.getString(R.string.app_name)),
            defaultBandwidthMeter
        )
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()

        return ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
            .setLoadErrorHandlingPolicy(this)
            .createMediaSource(uri)
    }
}