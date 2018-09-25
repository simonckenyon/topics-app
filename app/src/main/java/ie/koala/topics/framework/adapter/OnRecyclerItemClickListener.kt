package ie.koala.topics.framework.adapter

import androidx.recyclerview.widget.RecyclerView

interface OnRecyclerItemClickListener : BaseRecyclerListener {

    /**
     * Returns clicked item position [RecyclerView.ViewHolder.getAdapterPosition]
     *
     * @param position clicked item position.
     */
    fun onItemClick(position: Int)

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder)
}