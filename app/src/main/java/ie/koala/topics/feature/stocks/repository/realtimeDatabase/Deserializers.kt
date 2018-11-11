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

import com.google.firebase.database.DataSnapshot
import ie.koala.topics.feature.stocks.model.StockPrice
import ie.koala.topics.feature.stocks.repository.Deserializer
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat

internal interface DataSnapshotDeserializer<T> : Deserializer<DataSnapshot, T>

internal class StockPriceSnapshotDeserializer : DataSnapshotDeserializer<StockPrice> {
    override fun deserialize(input: DataSnapshot): StockPrice {
        val data = input.value
        log.debug("deserialize: data=$data")
        return if (data is Map<*, *>) {
            StockPrice(
                    input.key!!,
                    data["price"] as Double,
                    SimpleDateFormat("yyyy.MM.dd hh:mm:ss").parse(data["time"] as String),
                    true
            )
        } else {
            throw Deserializer.DeserializerException("input.value wasn't a Map")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockPriceSnapshotDeserializer::class.java)
    }
}
