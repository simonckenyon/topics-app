package ie.koala.topics.feature.topic

import android.content.Context
import android.view.ViewGroup

import ie.koala.topics.R
import ie.koala.topics.app.adapter.GenericRecyclerViewAdapter
import ie.koala.topics.app.adapter.OnRecyclerItemClickListener
import org.slf4j.LoggerFactory

class TopicListAdapter(context: Context, listener: OnRecyclerItemClickListener) : GenericRecyclerViewAdapter<Topic, OnRecyclerItemClickListener, TopicViewHolder>(context, listener) {

    lateinit var topicListener: TopicListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder(inflate(R.layout.item_topic, parent), listener!!)
    }

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and **not** at the end of a "drop" event.<br></br>
     * <br></br>
     * Implementations should call [RecyclerView.Adapter.notifyItemMoved] after
     * adjusting the underlying data to reflect this move.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then resolved position of the moved item.
     * @return True if the item was moved to the new adapter position.
     *
     * @see RecyclerView.getAdapterPositionFor
     * @see RecyclerView.ViewHolder.getAdapterPosition
     */
    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        move(fromPosition, toPosition)
        topicListener.onItemMoved(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    /**
     * Called when an item has been dismissed by a swipe.<br></br>
     * <br></br>
     * Implementations should call [RecyclerView.Adapter.notifyItemRemoved] after
     * adjusting the underlying data to reflect this removal.
     *
     * @param position The position of the item dismissed.
     *
     * @see RecyclerView.getAdapterPositionFor
     * @see RecyclerView.ViewHolder.getAdapterPosition
     */
    override fun onItemDismiss(position: Int) {
        val topic: Topic = removeAt(position)
        topicListener.onItemDeleted(topic)
        notifyItemRemoved(position)
        //
    }

    companion object {
        private val log = LoggerFactory.getLogger(TopicListAdapter::class.java)
    }
}
