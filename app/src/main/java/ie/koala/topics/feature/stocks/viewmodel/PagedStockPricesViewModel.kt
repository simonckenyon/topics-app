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

package ie.koala.topics.feature.stocks.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.annotation.MainThread
import ie.koala.topics.feature.stocks.model.StockPrice
import ie.koala.topics.feature.stocks.repository.QueryItemOrException
import ie.koala.topics.feature.stocks.repository.StockRepository
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.LoggerFactory

class PagedStockPricesViewModel : ViewModel(), KoinComponent {

    private val stockRepo by inject<StockRepository>()

    private var stockPricesLiveData: LiveData<PagedList<QueryItemOrException<StockPrice>>>? = null

    @MainThread
    fun getAllStockPricesPagedListLiveData(): LiveData<PagedList<QueryItemOrException<StockPrice>>> {
        log.debug("getAllStockPricesPagedListLiveData:")
        var liveData = stockPricesLiveData
        if (liveData == null) {
            // 5 is a ridiculously low page size in practice
            liveData = stockRepo.getStockPricePagedListLiveData(5)
            stockPricesLiveData = liveData
        }
        log.debug("getAllStockPricesPagedListLiveData: liveData=$liveData")
        return liveData
    }

    companion object {
        private val log = LoggerFactory.getLogger(PagedStockPricesViewModel::class.java)
    }

}
