package guinea.diego.launchervideoinnovation.ui.playback

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import guinea.diego.launchervideoinnovation.R
import kotlinx.android.synthetic.main.exoplayerview.*


class PlaybackActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exoplayerview)
        val url = this.intent.getStringExtra("videoUrl")
        videoReproductor.prepare(Uri.parse(url))
    }

    override fun onPause() {
        super.onPause()
        videoReproductor.onPause()
    }

    override fun onResume() {
        super.onResume()
        videoReproductor.onResume()
    }
}