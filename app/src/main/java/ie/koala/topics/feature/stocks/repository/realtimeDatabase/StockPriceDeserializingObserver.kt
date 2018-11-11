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

package ie.koala.topics.feature.stocks.repository.realtimeDatabase

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import ie.koala.topics.feature.stocks.livedata.rtdb.DataSnapshotOrException
import ie.koala.topics.feature.stocks.model.StockPrice
import ie.koala.topics.feature.stocks.repository.QueryResultsOrException
import ie.koala.topics.feature.stocks.repository.StockPriceQueryItem
import org.slf4j.LoggerFactory

// TODO Kick off to executor via subclass
// generify better
internal class StockPriceDeserializingObserver(
    private val liveData: MediatorLiveData<QueryResultsOrException<StockPrice, Exception>>,
    private val deserializer: StockPriceSnapshotDeserializer
) : Observer<DataSnapshotOrException> {

    override fun onChanged(results: DataSnapshotOrException?) {
        if (results != null) {
            val snapshot = results.data
            val exception = results.exception
            if (snapshot != null) {
                val items = snapshot.children.map { child ->
                    val stock = deserializer.deserialize(child)
                    log.debug("onChanged: stock=$stock")
                    StockPriceQueryItem(stock, child.key!!)
                }
                liveData.postValue(QueryResultsOrException(items, null))
            }
            else if (exception != null) {
                liveData.postValue(QueryResultsOrException(null, exception))
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockPriceDeserializingObserver::class.java)
    }

}
