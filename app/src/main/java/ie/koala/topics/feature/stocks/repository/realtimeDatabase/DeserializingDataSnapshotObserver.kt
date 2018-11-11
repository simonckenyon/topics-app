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

import androidx.lifecycle.Observer
import androidx.lifecycle.MediatorLiveData
import ie.koala.topics.feature.stocks.config.AppExecutors
import ie.koala.topics.feature.stocks.livedata.rtdb.DataSnapshotOrException
import ie.koala.topics.feature.stocks.common.DataOrException
import ie.koala.topics.feature.stocks.repository.Deserializer
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.LoggerFactory

internal class DeserializingDataSnapshotObserver<T>(
        private val deserializer: DataSnapshotDeserializer<T>,
        private val liveData: MediatorLiveData<DataOrException<T, Exception>>
) : Observer<DataSnapshotOrException>, KoinComponent {

    private val executors by inject<AppExecutors>()

    override fun onChanged(result: DataSnapshotOrException?) {
        if (result != null) {
            val snapshot = result.data
            val exception = result.exception
            log.debug("onChanged: snapshot=$snapshot exception=$exception")
            if (snapshot != null) {
                // Do this in a thread because DataSnapshot
                // deserialization with reflection can be costly.
                executors.cpuExecutorService.execute {
                    try {
                        val value = deserializer.deserialize(snapshot)
                        log.debug("execute: value=$value")
                        liveData.postValue(DataOrException(value, null))
                    } catch (e: Deserializer.DeserializerException) {
                        log.error("execute: exception ", e)
                        liveData.postValue(DataOrException(null, e))
                    }
                }
            } else if (exception != null) {
                liveData.value = DataOrException(null, exception)
            }
        } else {
            log.debug("onChanged: result is null")
            liveData.value = null
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DeserializingDataSnapshotObserver::class.java)
    }

}
