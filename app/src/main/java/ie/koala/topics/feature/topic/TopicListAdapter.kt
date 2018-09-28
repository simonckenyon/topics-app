package ie.koala.topics.feature.topic

import android.content.Context
import android.view.ViewGroup

import ie.koala.topics.R
import ie.koala.topics.adapter.GenericRecyclerViewAdapter
import ie.koala.topics.adapter.OnRecyclerItemClickListener

class TopicListAdapter(context: Context, listener: OnRecyclerItemClickListener) : GenericRecyclerViewAdapter<Topic, OnRecyclerItemClickListener, TopicViewHolder>(context, listener) {

    lateinit var topicListener: TopicListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder(inflate(R.layout.item_topic, parent), listener!!)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        move(fromPosition, toPosition)
        topicListener.onItemMoved(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        val topic: Topic = removeAt(position)
        topicListener.onItemDeleted(topic)
        notifyItemRemoved(position)
    }
}
