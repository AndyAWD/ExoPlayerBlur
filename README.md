# ExoPlayerBlur
***

使用[ExoPlayer](https://github.com/google/ExoPlayer)播放影片，並利用Bitmap完成影片模糊，ExoPlayer版本 2.10.5

## Base Setting
````
//build.gradle
defaultConfig {
    renderscriptTargetApi 18
    renderscriptSupportModeEnabled true
}
````

````
//proguard-rules.pro
# RenderScript
-keepclasseswithmembernames class * {
native <methods>;
}
-keep class androidx.renderscript.** { *; }
````

````
//AndroidManifest.xml
<uses-permission android:name="android.permission.INTERNET" />

<application
    android:networkSecurityConfig="@xml/network_security_config"
</application>
````
````
//network_security_config.xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
````

***

## Sample Usage

### STEP1
Xml新增TextureView、ImageView，PlayerView要新增surface_type="texture_view"
````
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.google.android.exoplayer2.ui.PlayerView
        android:id="@+id/playerView"
        android:visibility="gone"
        app:resize_mode="zoom"
        app:surface_type="texture_view"/>

    <TextureView
        android:id="@+id/textureView"
        android:visibility="visible"/>

    <ImageView
        android:id="@+id/imageView"
        android:visibility="visible"/>
</androidx.constraintlayout.widget.ConstraintLayout>
````

### STEP2
````
private var textureView: TextureView? = null
private var imageView: ImageView? = null
private var simpleExoPlayer: SimpleExoPlayer? = null

private fun initExoPlayer() {
    val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
    val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    val loadControl = DefaultLoadControl()
    simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, trackSelector, loadControl)

    simpleExoPlayer?.prepare(mediaSource("VideoUrl"))
    simpleExoPlayer?.addListener(this)
    simpleExoPlayer?.playWhenReady = true
    simpleExoPlayer?.repeatMode = Player.REPEAT_MODE_ALL

    textureView.alpha = 0.0f    //把TextureView設為透明，這樣模糊失敗的話也看不到影片
}

private fun mediaSource(uri: Uri): ProgressiveMediaSource {
    val defaultBandwidthMeter: DefaultBandwidthMeter = DefaultBandwidthMeter.Builder(applicationContext).build()
    val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(applicationContext, Util.getUserAgent(applicationContext, resources.getString(R.string.app_name)), defaultBandwidthMeter)
    val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()

    return ProgressiveMediaSource.Factory(dataSourceFactory,extractorsFactory).setLoadErrorHandlingPolicy(this).createMediaSource(uri)
}
````

### STEP3
偷懶使用別人寫好的模糊方法[EasyBlur](https://github.com/pinguo-zhouwei/EasyBlur)
````kotlin
textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        return
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        //使用EasyBlur處理Bitmap
        val bitmapBlur = EasyBlur.with(EBApplication.context()).bitmap(textureView.bitmap).scale(10).radius(25).blur()
        imageView.setImageBitmap(bitmapBlur)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        simpleExoPlayer.setVideoSurface(Surface(surface))
    }
}
````

## Special Thanks to
* [ExoPlayerFilter](https://github.com/MasayukiSuda/ExoPlayerFilter)
* [ExoPlayerVideoBlur](https://github.com/liosen/ExoPlayerVideoBlur)
