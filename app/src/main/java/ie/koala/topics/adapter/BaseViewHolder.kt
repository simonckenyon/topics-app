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
package ie.koala.topics.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import android.view.View

/**
 * Base ViewHolder to be used with the generic adapter.
 * [GenericRecyclerViewAdapter]
 *
 * @param <T> type of objects, which will be used in the adapter's data set
 * @param <L> click listener [BaseRecyclerListener]
 * @author Leonid Ustenko (Leo.Droidcoder@gmail.com)
 * @since 1.0.0
</L></T> */
abstract class BaseViewHolder<T, L : BaseRecyclerListener> : androidx.recyclerview.widget.RecyclerView.ViewHolder {

    lateinit var listener: L

    constructor(itemView: View) : super(itemView) {}

    constructor(itemView: View, listener: L) : super(itemView) {
        this.listener = listener
    }

    /**
     * Bind data to the item.
     * Make sure not to perform any expensive operations here.
     *
     * @param item object, associated with the item.
     * @author Leonid Ustenko (Leo.Droidcoder@gmail.com)
     * @since 1.0.0
     */
    abstract fun onBind(item: T)

    /**
     * Bind data to the item.
     * Override this method for using the payloads in order to achieve the full power of DiffUtil
     * [android.support.v7.util.DiffUtil.Callback]
     *
     * @param item object, associated with the item.
     * @author Leonid Ustenko (Leo.Droidcoder@gmail.com)
     * @since 1.0.0
     */
    fun onBind(item: T, @Suppress("UNUSED_PARAMETER") payloads: MutableList<Any>) {
        onBind(item)
    }


    /**
     * Called when the [ItemTouchHelper] first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    abstract fun onItemSelected()


    /**
     * Called when the [ItemTouchHelper] has completed the move or swipe, and the active item
     * state should be cleared.
     */
    abstract fun onItemClear()

}