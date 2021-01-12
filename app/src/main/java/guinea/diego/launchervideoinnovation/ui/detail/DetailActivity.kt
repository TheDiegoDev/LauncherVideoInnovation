package guinea.diego.launchervideoinnovation.ui.detail

import android.app.Activity
import android.os.Bundle
import guinea.diego.launchervideoinnovation.R

class DetailActivity: Activity() {

    companion object {
        const val KEY_VIDEO = "VIDEO"
        const val SHARED_ELEMENT_NAME = "hero"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
    }
}