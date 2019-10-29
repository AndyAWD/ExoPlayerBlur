package tw.com.andyawd.exoplayerblur

import android.app.Application
import tw.com.andyawd.andyawdlibrary.AWDConstants
import tw.com.andyawd.andyawdlibrary.AWDLog

class EBApplication : Application() {

    init {
        context = this
    }

    companion object {
        private var context: EBApplication? = null

        fun context(): EBApplication {
            return context as EBApplication
        }
    }

    override fun onCreate() {
        super.onCreate()

        AWDLog.setLogLevel(AWDConstants.LOG_VERBOSE)
    }
}