package tw.com.andyawd.exoplayerblur

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val exoNormalFragment = ExoNormalFragment()
    private val exoBlurFragment = ExoBlurFragment()
    private val videoNormalFragment = VideoNormalFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFragment()
        initListener()
    }

    private fun initFragment() {

    }

    @SuppressLint("CheckResult")
    private fun initListener() {
        tvAmExoPlayerNormalPlay.clicks().throttleFirst(Constants.CLICK_TIMER, TimeUnit.MILLISECONDS)
            .subscribe(tvAmExoPlayerNormalPlayClick)
        tvAmExoPlayerBlurPlay.clicks().throttleFirst(Constants.CLICK_TIMER, TimeUnit.MILLISECONDS)
            .subscribe(tvAmExoPlayerBlurPlayClick)
        tvAmVideoViewNormaPlay.clicks().throttleFirst(Constants.CLICK_TIMER, TimeUnit.MILLISECONDS)
            .subscribe(tvAmVideoViewNormaPlayClick)
    }


    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    private val tvAmExoPlayerNormalPlayClick = Consumer<Any> {
        supportFragmentManager.inTransaction {
            replace(R.id.clAmVideoGroup, exoNormalFragment)
        }
    }

    private val tvAmExoPlayerBlurPlayClick = Consumer<Any> {
        supportFragmentManager.inTransaction {
            replace(R.id.clAmVideoGroup, exoBlurFragment)
        }
    }

    private val tvAmVideoViewNormaPlayClick = Consumer<Any> {
        supportFragmentManager.inTransaction {
            replace(R.id.clAmVideoGroup, videoNormalFragment)
        }
    }
}

