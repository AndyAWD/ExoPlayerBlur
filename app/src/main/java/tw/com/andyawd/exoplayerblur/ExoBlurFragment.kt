package tw.com.andyawd.exoplayerblur

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
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
import com.jakewharton.rxbinding3.view.clicks
import com.zhouwei.blurlibrary.EasyBlur
import tw.com.andyawd.andyawdlibrary.AWDPopupWindowMgr
import java.io.IOException
import java.util.concurrent.TimeUnit

class ExoBlurFragment : Fragment(), LoadErrorHandlingPolicy, Player.EventListener,
        TextureView.SurfaceTextureListener {

    private var pvFebVideo: PlayerView? = null
    private var tvFebVideo: TextureView? = null
    private var ivFebVideoBlur: ImageView? = null
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var isPlaying = false

    private var blurAdjustmentWindow: AWDPopupWindowMgr? = null
    private var tvFebBlurText: TextView? = null
    private var sbFebBlur: SeekBar? = null
    private var tvFebCompressionText: TextView? = null
    private var sbFebCompression: SeekBar? = null

    private var blur = Constants.INIT_BLUR
    private var compression = Constants.INIT_COMPRESSION

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exo_blur, container, false)

        initComponent(view)
        initBlurAdjustment()
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

        sbFebBlur?.progress = blur
        sbFebCompression?.progress = compression
        setBlurAdjustmentText(blur, compression)
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
                EasyBlur.with(EBApplication.context()).bitmap(tvFebVideo?.bitmap).scale(compression).radius(blur)
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
        tvFebBlurText = view.findViewById(R.id.tvFebBlurText)
        sbFebBlur = view.findViewById(R.id.sbFebBlur)
        tvFebCompressionText = view.findViewById(R.id.tvFebCompressionText)
        sbFebCompression = view.findViewById(R.id.sbFebCompression)
    }

    private fun initBlurAdjustment() {
        blurAdjustmentWindow =
            AWDPopupWindowMgr.init(activity).setLayout(R.layout.view_blur_adjustment).build()
        tvFebBlurText = blurAdjustmentWindow?.findViewById(R.id.tvFebBlurText) as TextView
        sbFebBlur = blurAdjustmentWindow?.findViewById(R.id.sbFebBlur) as SeekBar
        tvFebCompressionText =
            blurAdjustmentWindow?.findViewById(R.id.tvFebCompressionText) as TextView
        sbFebCompression = blurAdjustmentWindow?.findViewById(R.id.sbFebCompression) as SeekBar
    }

    @SuppressLint("CheckResult")
    private fun initListener() {
        tvFebVideo?.surfaceTextureListener = this
        sbFebBlur?.setOnSeekBarChangeListener(sbFebBlurSeekBarChange)
        sbFebCompression?.setOnSeekBarChangeListener(sbFebCompressionSeekBarChange)

        ivFebVideoBlur?.clicks()?.throttleFirst(Constants.CLICK_TIMER, TimeUnit.MILLISECONDS)
            ?.subscribe { blurAdjustmentWindow?.showAtLocation(R.layout.fragment_exo_blur) }
    }

    private fun initExoPlayer() {
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl()
        simpleExoPlayer =
                ExoPlayerFactory.newSimpleInstance(EBApplication.context(), trackSelector, loadControl)

        simpleExoPlayer?.prepare(mediaSource(VideoPathManager.instance.exoPlayerRaw(R.raw.mv)))
        simpleExoPlayer?.addListener(this)
        simpleExoPlayer?.playWhenReady = true
        simpleExoPlayer?.repeatMode = Player.REPEAT_MODE_ALL

        tvFebVideo?.alpha = 0.0f    //把TextureView設為透明，這樣模糊失敗的話也看不到影片
        isPlaying = simpleExoPlayer?.playWhenReady!!
    }

    private fun mediaSource(uri: Uri): ProgressiveMediaSource {
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

    private val sbFebBlurSeekBarChange = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            if (0 == p1) {
                return
            }

            blur = p1
            setBlurAdjustmentText(p1, compression)
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
            return
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
            return
        }
    }

    private val sbFebCompressionSeekBarChange = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            if (0 == p1) {
                return
            }

            compression = p1
            setBlurAdjustmentText(blur, p1)
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {

        }

        override fun onStopTrackingTouch(p0: SeekBar?) {

        }
    }

    private fun setBlurAdjustmentText(blur: Int, compression: Int) {
        tvFebBlurText?.text =
            EBApplication.context().resources.getString(R.string.blurText, blur, Constants.MAX_BLUR)
        tvFebCompressionText?.text = EBApplication.context().resources.getString(
            R.string.compressionText,
            compression,
            Constants.MAX_COMPRESSION
        )
    }
}