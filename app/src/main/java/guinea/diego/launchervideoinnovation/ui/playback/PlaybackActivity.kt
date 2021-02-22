package guinea.diego.launchervideoinnovation.ui.playback

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import guinea.diego.launchervideoinnovation.R
import kotlinx.android.synthetic.main.exoplayerview.*


class PlaybackActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exoplayerview)
        val url = this.intent.getStringExtra("videoUrl")
        videoReproductor.prepare(Uri.parse(url))
        closeAct()
    }
    private fun closeAct(){
        val close_btn = findViewById<Button>(R.id.exo_close)
        close_btn.setOnClickListener {
            finish()
        }
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