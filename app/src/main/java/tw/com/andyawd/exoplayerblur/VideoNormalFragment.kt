package tw.com.andyawd.exoplayerblur

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import tw.com.andyawd.andyawdlibrary.AWDLog

class VideoNormalFragment : Fragment(), MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener {

    private var vvFvnVideo: VideoView? = null
    private var pbFvnVideoLoading: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video_normal, container, false)
        AWDLog.d("VideoView正常播放 onCreateView")

        initComponent(view)
        initListener()
        initVideoView()

        return view
    }

    private fun initComponent(view: View) {
        vvFvnVideo = view.findViewById(R.id.vvFvnVideo)
        pbFvnVideoLoading = view.findViewById(R.id.pbFvnVideoLoading)
    }

    private fun initListener() {
        vvFvnVideo?.setOnErrorListener(this)
        vvFvnVideo?.setOnPreparedListener(this)
        vvFvnVideo?.setOnCompletionListener(this)
    }

    private fun initVideoView() {
        pbFvnVideoLoading?.visibility = View.VISIBLE

        val mediaController = MediaController(activity)
        vvFvnVideo?.setMediaController(mediaController)
        vvFvnVideo?.setVideoURI(Constants.VIDEO_URL.toUri())
    }

    override fun onStart() {
        super.onStart()
        AWDLog.d("VideoView正常播放 onStart")

        vvFvnVideo?.start()
    }

    override fun onResume() {
        super.onResume()
        AWDLog.d("VideoView正常播放 onResume")

        vvFvnVideo?.resume()
    }

    override fun onPause() {
        super.onPause()
        AWDLog.d("VideoView正常播放 onPause")

        vvFvnVideo?.pause()
    }

    override fun onStop() {
        super.onStop()
        AWDLog.d("VideoView正常播放 onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        AWDLog.d("VideoView正常播放 onDestroy")
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_ERROR_IO -> {
                AWDLog.d("MEDIA_ERROR_IO")
            }
            MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                AWDLog.d("MEDIA_ERROR_MALFORMED")
            }
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                AWDLog.d("MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK")
            }
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> {
                AWDLog.d("MEDIA_ERROR_SERVER_DIED")
            }
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                AWDLog.d("MEDIA_ERROR_TIMED_OUT")
            }
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                AWDLog.d("MEDIA_ERROR_UNSUPPORTED")
            }
            else -> {
                AWDLog.d("else")
            }
        }
        return true
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.setOnInfoListener { mp, what, extra ->
            if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                pbFvnVideoLoading?.visibility = View.GONE
                return@setOnInfoListener true
            }
            return@setOnInfoListener false
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
        mp.start()
    }
}
