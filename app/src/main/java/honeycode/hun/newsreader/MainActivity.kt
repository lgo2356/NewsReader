package honeycode.hun.newsreader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.NodeList
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IndexOutOfBoundsException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    private val mData = ArrayList<NewsListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)

        val newsListAdapter = NewsListAdapter(this, mData)
        news_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        news_list.adapter = newsListAdapter

        val newsDocs = ArrayList<Document>()
        GlobalScope.launch(Dispatchers.Main) {
            val httpCoroutine = async(Dispatchers.IO) {
                val inputStream: InputStream = getInputStream(URL("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"))
                val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                val nodeList: NodeList = docBuilder.parse(inputStream).documentElement.getElementsByTagName("item")
                for(i in 0 until 10) {  // 완료된 아이템 부터 리스트 업하는 방법 검토
                    try {
                        Log.d("TAG", "IO Index: $i")
                        val newsArticleUrl: String = nodeList.item(i).childNodes.item(1).childNodes.item(0).nodeValue
                        newsDocs.add(Jsoup.connect(newsArticleUrl).get())
//                        val newsThumbnailUrl = jsoupDocument.select("meta[property=og:image]")[0].attr("content")
//                        val newsTitle: String = nodeList.item(i).childNodes.item(0).childNodes.item(0).nodeValue
//                        val newsDescription = jsoupDocument.select("meta[property=og:description]")[0].attr("content")
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                }
            }
            val testCoroutine = async(Dispatchers.Default) {
                try {
                    var i  = 0
                    while (true) {
                        if(newsDocs.size > newsListAdapter.itemCount) {
                            Log.d("TAG", "Test Index: $i")
                            val newsThumbnailUrl = newsDocs[i].select("meta[property=og:image]")[0].attr("content")
                            val newsDescription = newsDocs[i].select("meta[property=og:description]")[0].attr("content")
                            addNewsItem(newsThumbnailUrl, "TEST", newsDescription)
//                            newsListAdapter.notifyDataSetChanged()
                            i += 1
                        }

                        if(i >= 9) break
                    }
//                    for(newsDoc in newsDocs) {
//                        val newsThumbnailUrl = newsDoc.select("meta[property=og:image]")[0].attr("content")
//                        val newsDescription = newsDoc.select("meta[property=og:description]")[0].attr("content")
//                        addNewsItem(newsThumbnailUrl, "TEST", newsDescription)
//                    }
                } catch (e: IndexOutOfBoundsException) {e.printStackTrace()}
//                val newsThumbnailUrl = newsArticleUrlList[0].select("meta[property=og:image]")[0].attr("content")
//                val newsDescription = newsArticleUrlList[0].select("meta[property=og:description]")[0].attr("content")
            }
            testCoroutine.await()

//            val ioCoroutine2 = async(Dispatchers.IO) {
//                val inputStream: InputStream = getInputStream(URL("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"))
//                val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
//                val nodeList: NodeList = docBuilder.parse(inputStream).documentElement.getElementsByTagName("item")
//
//                for(i in 10 until 20) {
//                    try {
//                        Log.d("TAG", "Index: $i")
//                        val newsArticleUrl: String = nodeList.item(i).childNodes.item(1).childNodes.item(0).nodeValue
//                        newsDocs.add(Jsoup.connect(newsArticleUrl).get())
//                    } catch (e: IndexOutOfBoundsException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
            httpCoroutine.await()
//            ioCoroutine2.await()
            Log.d("TAG", "item count: ${newsListAdapter.itemCount}")
            newsListAdapter.notifyDataSetChanged()
        }

        Log.d("TAG", "done in main thread")
    }

    private fun addNewsItem(pThumbnail: String?, pTitle: String?, pContent: String?) {
        val item = NewsListItem(pThumbnail, pTitle, pContent)
        mData.add(item)
    }

    private fun getInputStream(url: URL): InputStream {
        var conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        Log.d("TAG", "Response Code: ${conn.responseCode}")

        return when(conn.responseCode) {
            HttpURLConnection.HTTP_OK -> conn.inputStream
            HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM -> {
                val redirectedUrl = conn.getHeaderField("Location")
                conn = URL(redirectedUrl).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.inputStream
            }
            else -> conn.inputStream
        }
    }

    private fun loadHtml(url: URL): String {
        var conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        Log.d("TAG", "Http Response Code: ${conn.responseCode}")

        return when(conn.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                val buffer = StringBuffer()
                val bufferedReader = InputStreamReader(conn.inputStream, "utf-8")
                for(line in bufferedReader.readText()) buffer.append(line)
                bufferedReader.close()
                buffer.toString()
            }
            HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM -> {
                val redirectedUrl: String = conn.getHeaderField("Location")
                conn = URL(redirectedUrl).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                val bufferedReader = InputStreamReader(conn.inputStream, "utf-8")
                val buffer = StringBuffer()
                for(line in bufferedReader.readText()) buffer.append(line)
                bufferedReader.close()
                buffer.toString()
            }
            else -> "Html 로드를 실패했습니다."
        }
    }
}
