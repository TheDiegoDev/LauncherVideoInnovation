package guinea.diego.launchervideoinnovation.ui.home


import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import guinea.diego.launchervideoinnovation.R



class HomeActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}