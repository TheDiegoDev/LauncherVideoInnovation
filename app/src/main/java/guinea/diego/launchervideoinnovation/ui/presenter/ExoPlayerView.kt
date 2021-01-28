package guinea.diego.launchervideoinnovation.ui.presenter

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

class ExoPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defSystem: Int = 0
)
    :FrameLayout(context, attrs, defSystem), Player.EventListener {

        private var simpleExoPlayer: SimpleExoPlayer

        init {
            val playerView = PlayerView(context)
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            playerView.useController = false
            addView(playerView)

            simpleExoPlayer = SimpleExoPlayer.Builder(context).build()
            simpleExoPlayer.addListener(this)
            simpleExoPlayer.playWhenReady = true
            playerView.player = simpleExoPlayer
        }
    fun prepare(uri: Uri){
        val timeout = 10000
        val dataSourceFactory = DefaultHttpDataSourceFactory(
            Util.getUserAgent(
                context,
                "ExoPLayerView"
            ),
            timeout, timeout, true
        )

        val mediaSource = when(Util.inferContentType(uri)){
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory)
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
            else -> throw  Exception("Fuente desconocida")
        }.createMediaSource(uri)

        simpleExoPlayer.prepare(mediaSource)
    }
    fun onPause(){
        simpleExoPlayer.playWhenReady = false
    }
    fun onResume(){
        simpleExoPlayer.playWhenReady = true
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Log.e("ExoPlayer", "Error: ", error)
    }
}