package guinea.diego.launchervideoinnovation.ui.webView



import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.webkit.WebViewClient
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import guinea.diego.launchervideoinnovation.R
import guinea.diego.launchervideoinnovation.ui.detail.DetailActivity
import kotlinx.android.synthetic.main.activity_web.*


class WebView: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_web)
        setView()
    }

    private fun setView(){
        val close_btn = findViewById<Button>(R.id.web_close)
        val titulo = this.intent.getStringExtra("titulo").toString()
        val url = this.intent.getStringExtra("url").toString()
        //webview.settings.javaScriptEnabled = true
        //textView_title.text = titulo
        webview.webViewClient = WebViewClient()
        webview.loadUrl(url)

        close_btn.setOnClickListener {
           finish()
        }

    }
}