package guinea.diego.launchervideoinnovation.ui.webView



import android.os.Bundle
import android.view.Window
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import guinea.diego.launchervideoinnovation.R
import kotlinx.android.synthetic.main.activity_web.*


class WebView: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_web)
        setView()
    }

    private fun setView(){
        val url = this.intent.getStringExtra("url").toString()
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = WebViewClient()
        webview.loadUrl(url)


    }
}