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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.news_list_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.newsThumbnail.setImageDrawable(mData[position].thumbnailUrl)
        Glide.with(context).load(mData[position].thumbnailUrl).into(holder.newsThumbnail)
        holder.newsTitle.text = mData[position].title
        holder.newsContent.text = mData[position].content
    }

    override fun getItemCount() = mData.size
}

data class NewsListItem(var thumbnailUrl: String?, var title: String?, var content: String?)
