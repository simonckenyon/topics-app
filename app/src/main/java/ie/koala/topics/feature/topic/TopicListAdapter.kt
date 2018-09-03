package ie.koala.topics.feature.topic

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ie.koala.topics.R
import ie.koala.topics.feature.topic.Topic.Factory.ARG_TOPIC
import kotlinx.android.synthetic.main.row_item.view.*

class TopicListAdapter(private val parentActivity: TopicListActivity,
                       private val topicList: MutableList<Topic>) : RecyclerView.Adapter<TopicListAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val topic = v.tag as Topic
            val intent = Intent(v.context, TopicDetailActivity::class.java).apply {
                putExtra(ARG_TOPIC, topic)
            }
            v.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic: Topic = topicList[position]
        val title: String? = topic.title
        if (title != null) {
            holder.bind(title, topic)
        }
    }

    override fun getItemCount() = topicList.size

    fun addItem(topic: Topic) {
        topicList.add(topic)
        notifyItemInserted(topicList.size)
    }

    fun removeAt(position: Int): Topic {
        val topic: Topic = topicList.removeAt(position)
        notifyItemRemoved(position)
        return topic
    }

    inner class ViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(view.context).inflate(R.layout.row_item, view, false)) {

        fun bind(name: String, topic: Topic) = with(itemView) {
            rowName.text = name
            tag = topic
            setOnClickListener(onClickListener)
        }
    }
}

