package ie.koala.topics.feature.stocks.diffcallback

import androidx.recyclerview.widget.DiffUtil
import ie.koala.topics.feature.stocks.repository.QueryItemOrException

/**
 * T must be a data class for this to work, as it depends on the structural
 * equality to compare objects.
 */

class QueryItemOrExceptionDiffUtilItemCallback<T> : DiffUtil.ItemCallback<QueryItemOrException<T>>() {

    override fun areItemsTheSame(oldItem: QueryItemOrException<T>, newItem: QueryItemOrException<T>): Boolean {
        return if (oldItem.data != null && newItem.data != null) {
            oldItem.data.id == newItem.data.id
        } else {
            oldItem === newItem
        }
    }

    override fun areContentsTheSame(oldItem: QueryItemOrException<T>, newItem: QueryItemOrException<T>): Boolean {
        return oldItem.data == newItem.data
    }

}
