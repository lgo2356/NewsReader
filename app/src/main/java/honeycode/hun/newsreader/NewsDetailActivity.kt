package honeycode.hun.newsreader

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        title = intent.getStringExtra("title")
        first_keyword.text = intent.getStringExtra("first")
        second_keyword.text = intent.getStringExtra("second")
        third_keyword.text = intent.getStringExtra("third")

        val webView: WebView = findViewById(R.id.news_detail_view)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(intent.getStringExtra("news_url"))
    }
}