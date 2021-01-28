package guinea.diego.launchervideoinnovation.ui.home


import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.fragment.app.FragmentActivity
import guinea.diego.launchervideoinnovation.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exoplayerview.*


class HomeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}