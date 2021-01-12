package guinea.diego.launchervideoinnovation.ui.playback

import android.os.Bundle
import androidx.fragment.app.FragmentActivity


class PlaybackActivity: FragmentActivity() {

    companion object {
        const val KEY_VIDEO = "VIDEO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, PlaybackFragment())
                .commit()
    }
}