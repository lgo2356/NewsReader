package honeycode.hun.newsreader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.NodeList
import java.io.InputStream
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    private val mDataList = ArrayList<NewsListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val splashIntent = Intent(this, SplashActivity::class.java)
        startActivity(splashIntent)
        val newsDetailIntent = Intent(this, NewsDetailActivity::class.java)

        val newsListAdapter = NewsListAdapter(this, mDataList)
        news_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        news_list.adapter = newsListAdapter

        newsListAdapter.setItemClickListener(object: NewsListAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                newsDetailIntent.putExtra("news_url", mDataList[position].newsUrl)
                newsDetailIntent.putExtra("title", mDataList[position].title)
                newsDetailIntent.putExtra("first", mDataList[position].first)
                newsDetailIntent.putExtra("second", mDataList[position].second)
                newsDetailIntent.putExtra("third", mDataList[position].third)
                startActivity(newsDetailIntent)
            }
        })

        val listUpCoroutine = CoroutineScope(Dispatchers.IO)
        listUpCoroutine.launch {
            val inputStream = getInputStream(URL("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"))
            val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val nodeList = documentBuilder.parse(inputStream).documentElement.getElementsByTagName("item")

            launch {
                for(i in 0 until 3) {
                    try {
                        val newsArticleUrl = nodeList.item(i).childNodes.item(1).childNodes.item(0).nodeValue
                        val newsTitle: String = nodeList.item(i).childNodes.item(0).childNodes.item(0).nodeValue

                        Log.d("TAG", "List $i news url: $newsArticleUrl")
                        val jDoc = Jsoup.connect(newsArticleUrl).get()
                        val newsDescription = getDescription(jDoc)
                        val newsThumbnailUrl = try { jDoc.select("meta[property=og:image]")[0].attr("content") } catch(e: Exception) { null }
                        val keywords = getKeywords(newsDescription)
                        addNewsItem(newsArticleUrl, newsThumbnailUrl, newsTitle, newsDescription, keywords[0], keywords[1], keywords[2])
                        launch(Dispatchers.Main) { newsListAdapter.notifyDataSetChanged() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            launch {
                for(i in 3 until 10) {
                    try {
                        val newsArticleUrl = nodeList.item(i).childNodes.item(1).childNodes.item(0).nodeValue
                        val newsTitle: String = nodeList.item(i).childNodes.item(0).childNodes.item(0).nodeValue

                        Log.d("TAG", "List $i news url: $newsArticleUrl")
                        val jDoc = Jsoup.connect(newsArticleUrl).get()
                        val newsDescription = getDescription(jDoc)
                        val newsThumbnailUrl = try { jDoc.select("meta[property=og:image]")[0].attr("content") } catch(e: Exception) { null }
                        val keywords = getKeywords(newsDescription)
                        addNewsItem(newsArticleUrl, newsThumbnailUrl, newsTitle, newsDescription, keywords[0], keywords[1], keywords[2])
                        launch(Dispatchers.Main) { newsListAdapter.notifyDataSetChanged() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            launch {
                for(i in 10 until nodeList.length) {
                    try {
                        val newsArticleUrl = nodeList.item(i).childNodes.item(1).childNodes.item(0).nodeValue
                        val newsTitle: String = nodeList.item(i).childNodes.item(0).childNodes.item(0).nodeValue

                        Log.d("TAG", "List $i news url: $newsArticleUrl")
                        val jDoc = Jsoup.connect(newsArticleUrl).get()
                        val newsDescription = getDescription(jDoc)
                        val newsThumbnailUrl = try { jDoc.select("meta[property=og:image]")[0].attr("content") } catch(e: Exception) { null }
                        val keywords = getKeywords(newsDescription)
                        addNewsItem(newsArticleUrl, newsThumbnailUrl, newsTitle, newsDescription, keywords[0], keywords[1], keywords[2])
                        launch(Dispatchers.Main) { newsListAdapter.notifyDataSetChanged() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        swipe_refresh_news_list.setOnRefreshListener {
            val updateCoroutine = CoroutineScope(Dispatchers.IO)
            updateCoroutine.launch {
                var updatedFlag = false
                val inputStream = getInputStream(URL("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"))
                val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                val nodeList = documentBuilder.parse(inputStream).documentElement.getElementsByTagName("item")

                val firstTitle: String = nodeList.item(0).childNodes.item(0).childNodes.item(0).nodeValue
                if(firstTitle != mDataList[0].title) {
                    listUpCoroutine.cancel()
                    updatedFlag = true
                }

                if(updatedFlag) {
                    for(i in newsListAdapter.itemCount.minus(1) downTo 0) {
                        removeNewsItem(i)
                    }
                    launch(Dispatchers.Main) { newsListAdapter.notifyDataSetChanged() }

                    launch {
                        for(i in 0 until 3) {
                            try {
                                val newsArticleUrl = nodeList.item(i).childNodes.item(1).childNodes.item(0).nodeValue
                                val newsTitle: String = nodeList.item(i).childNodes.item(0).childNodes.item(0).nodeValue

                                Log.d("TAG", "List $i news url: $newsArticleUrl")
                                val jDoc = Jsoup.connect(newsArticleUrl).get()
                                val newsDescription = getDescription(jDoc)
                                val newsThumbnailUrl = try { jDoc.select("meta[property=og:image]")[0].attr("content") } catch(e: Exception) { null }
                                val keywords = getKeywords(newsDescription)
                                addNewsItem(newsArticleUrl, newsThumbnailUrl, newsTitle, newsDescription, keywords[0], keywords[1], keywords[2])
                                launch(Dispatchers.Main) { newsListAdapter.notifyDataSetChanged() }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    launch {
                        for(i in 3 until 10) {
                            try {
                                val newsArticleUrl = nodeList.item(i).childNodes.item(1).childNodes.item(0).nodeValue
                                val newsTitle: String = nodeList.item(i).childNodes.item(0).childNodes.item(0).nodeValue

                                Log.d("TAG", "List $i news url: $newsArticleUrl")
                                val jDoc = Jsoup.connect(newsArticleUrl).get()
                                val newsDescription = getDescription(jDoc)
                                val newsThumbnailUrl = try { jDoc.select("meta[property=og:image]")[0].attr("content") } catch(e: Exception) { null }
                                val keywords = getKeywords(newsDescription)
                                addNewsItem(newsArticleUrl, newsThumbnailUrl, newsTitle, newsDescription, keywords[0], keywords[1], keywords[2])
                                launch(Dispatchers.Main) { newsListAdapter.notifyDataSetChanged() }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    launch {
                        for(i in 10 until nodeList.length) {
                            try {
                                val newsArticleUrl = nodeList.item(i).childNodes.item(1).childNodes.item(0).nodeValue
                                val newsTitle: String = nodeList.item(i).childNodes.item(0).childNodes.item(0).nodeValue

                                Log.d("TAG", "List $i news url: $newsArticleUrl")
                                val jDoc = Jsoup.connect(newsArticleUrl).get()
                                val newsDescription = getDescription(jDoc)
                                val newsThumbnailUrl = try { jDoc.select("meta[property=og:image]")[0].attr("content") } catch(e: Exception) { null }
                                val keywords = getKeywords(newsDescription)
                                addNewsItem(newsArticleUrl, newsThumbnailUrl, newsTitle, newsDescription, keywords[0], keywords[1], keywords[2])
                                launch(Dispatchers.Main) { newsListAdapter.notifyDataSetChanged() }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            Log.d("TAG", "News list refreshed.")
            swipe_refresh_news_list.isRefreshing = false
        }
    }

    private fun addNewsItem(pNewsUrl: String, pThumbnail: String?, pTitle: String, pContent: String, pFirst: String, pSecond: String, pThird: String) {
        val item = NewsListItem(pNewsUrl, pThumbnail, pTitle, pContent, pFirst, pSecond, pThird)
        mDataList.add(item)
    }

    private fun removeNewsItem(position: Int) {
        mDataList.removeAt(position)
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

    private fun getKeywords(desc: String): ArrayList<String> {
        val replaceWord = desc.replace("[,&#;\'\"‘’“”]".toRegex(), "")
        val words = replaceWord.split(" ", "\n") as MutableList<String>
        val iterator = words.iterator()
        while(iterator.hasNext()) {
            val word = iterator.next()
            if(word.length <= 1) iterator.remove()
        }

        val wordMap: MutableMap<String, Int?> = mutableMapOf()
        for(word in words) wordMap[word] = wordMap[word]?.plus(1)?:1
        val sortedByValue = wordMap.toList().sortedBy { it.first }.sortedByDescending { it.second }

        val result = ArrayList<String>()
        for(i in 0 until 3) result.add(sortedByValue[i].first)
        return result
    }

    private fun getDescription(jsoupDoc: Document): String {
        val propertyDesc = jsoupDoc.select("meta[property=og:description]")
        val newsDescription: String

        newsDescription = if(propertyDesc.size <= 0) {
            jsoupDoc.select("meta[name=description]")[0].attr("content")
        } else {
            propertyDesc[0].attr("content")
        }
        return newsDescription
    }
}
