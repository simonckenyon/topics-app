/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ie.koala.topics.feature.stocks

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import ie.koala.topics.databinding.ItemStocksBinding
import ie.koala.topics.feature.stocks.diffcallback.QueryItemOrExceptionDiffUtilItemCallback
import ie.koala.topics.feature.stocks.model.StockPrice
import ie.koala.topics.feature.stocks.repository.QueryItemOrException
import ie.koala.topics.feature.stocks.viewmodel.PagedStockPricesViewModel
import ie.koala.topics.feature.stocks.viewmodel.toStockPriceDisplay
import org.slf4j.LoggerFactory

internal class StocksPagedListAdapter(
        stockPriceViewModel: PagedStockPricesViewModel,
        lifecycleOwner: LifecycleOwner,
        private val itemClickListener: ItemClickListener<StockViewHolder>
) : PagedListAdapter<QueryItemOrException<StockPrice>, StockViewHolder>(QueryItemOrExceptionDiffUtilItemCallback()) {

    init {
        stockPriceViewModel.getAllStockPricesPagedListLiveData().observe(lifecycleOwner, Observer {
            log.debug("init: observer triggered")
            submitList(it)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        // Using data binding on the individual views
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStocksBinding.inflate(inflater, parent, false)
        val holder = StockViewHolder(binding)
        holder.itemClickListener = itemClickListener
        return holder
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        log.debug("onBindViewHolder: holder=$holder position=$position")
        val item = getItem(position)
        if (item?.data != null) {
            log.debug("onBindViewHolder: item=$item")
            val ticker = item.data.item.ticker
            val display = item.data.item.toStockPriceDisplay()
            holder.binding.stockPrice = display
            holder.ticker = ticker
        } else {
            log.debug("onBindViewHolder: item not found")
        }
    }

    internal interface ItemClickListener<StockViewHolder> {
        fun onItemClick(holder: StockViewHolder)
    }

    companion object {
        private val log = LoggerFactory.getLogger(StocksPagedListAdapter::class.java)
    }

}
