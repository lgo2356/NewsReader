package honeycode.hun.newsreader

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsListAdapter(private val context: Context, private val mData: ArrayList<NewsListItem>) : RecyclerView.Adapter<NewsListAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsThumbnail: ImageView = itemView.findViewById(R.id.news_thumbnail)
        val newsTitle: TextView = itemView.findViewById(R.id.news_title)
        val newsContent: TextView = itemView.findViewById(R.id.news_content)
        val newsKeyword1: TextView = itemView.findViewById(R.id.keyword_first)
        val newsKeyword2: TextView = itemView.findViewById(R.id.keyword_second)
        val newsKeyword3: TextView = itemView.findViewById(R.id.keyword_third)
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener =itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.news_list_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context)
            .load(mData[position].thumbnailUrl)
            .thumbnail(0.1f)
            .placeholder(R.drawable.main_splash_image)
            .error(R.drawable.main_splash_image)
            .into(holder.newsThumbnail)
        holder.newsTitle.text = mData[position].title
        holder.newsContent.text = mData[position].content
        holder.newsKeyword1.text = mData[position].first
        holder.newsKeyword2.text = mData[position].second
        holder.newsKeyword3.text = mData[position].third

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount() = mData.size
}

data class NewsListItem(var newsUrl: String, var thumbnailUrl: String?, var title: String, var content: String, var first: String, var second: String, var third: String)
