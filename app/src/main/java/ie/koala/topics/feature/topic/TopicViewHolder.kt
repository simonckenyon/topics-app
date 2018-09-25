/*
 * Copyright (C) 2017 Leonid Ustenko Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ie.koala.topics.feature.topic

import android.view.View
import android.widget.TextView
import ie.koala.topics.R


import ie.koala.topics.framework.adapter.BaseViewHolder
import ie.koala.topics.framework.adapter.OnRecyclerItemClickListener
import org.slf4j.LoggerFactory
import android.view.MotionEvent
import androidx.core.view.MotionEventCompat
import android.widget.ImageView


/**
 * A view holder implementation.
 *
 * @author Leonid Ustenko (Leo.Droidcoder@gmail.com)
 * @since 1.0.0
 */

class TopicViewHolder(itemView: View, listener: OnRecyclerItemClickListener) : BaseViewHolder<Topic, OnRecyclerItemClickListener>(itemView, listener) {

    private lateinit var title: TextView
    private lateinit var handle: ImageView

    init {
        initViews()
    }

    /**
     * Initialize views and set the listeners
     */
    private fun initViews() {
        title = itemView.findViewById(R.id.title)
        handle = itemView.findViewById(R.id.handle)
        (handle as View).setOnTouchListener { _, event ->
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                listener.onStartDrag(this)
            }
            false
        }
        itemView.setOnClickListener { _ -> listener.onItemClick(adapterPosition) }

        // Start a drag whenever the handle view it touched
    }

    override fun onBind(item: Topic) {
        // bind data to the views
        title.text = item.title
    }

    /**
     * Called when the [ItemTouchHelper] first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    override fun onItemSelected() {
        log.debug("onItemSelected: not implemented")
    }

    /**
     * Called when the [ItemTouchHelper] has completed the move or swipe, and the active item
     * state should be cleared.
     */
    override fun onItemClear() {
        log.debug("onItemClear: not implemented")
    }

    companion object {
        private val log = LoggerFactory.getLogger(TopicViewHolder::class.java)
    }

}