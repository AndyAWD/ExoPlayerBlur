package tw.com.andyawd.exoplayerblur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.io.IOException

class ExoNormalFragment : Fragment(), LoadErrorHandlingPolicy, Player.EventListener {

    private var pvFenVideo: PlayerView? = null

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exo_normal, container, false)

        initComponent(view)

        return view
    }

    override fun onStart() {
        super.onStart()

        initExoPlayer()
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

    private fun initComponent(view: View) {
        pvFenVideo = view.findViewById(R.id.pvFenVideo)
    }

    private fun initExoPlayer() {
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl()
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

        simpleExoPlayer =
            ExoPlayerFactory.newSimpleInstance(EBApplication.context(), trackSelector, loadControl)

        simpleExoPlayer?.prepare(progressiveMediaSource)
        simpleExoPlayer?.addListener(this)
        simpleExoPlayer?.playWhenReady = true
        simpleExoPlayer?.repeatMode = Player.REPEAT_MODE_ALL

        isPlaying = simpleExoPlayer?.playWhenReady!!

        pvFenVideo?.player = simpleExoPlayer
    }
}