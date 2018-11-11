/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ie.koala.topics.data

import androidx.lifecycle.MutableLiveData
import ie.koala.topics.BuildConfig
import ie.koala.topics.api.TopicService
import ie.koala.topics.api.allTopics
import ie.koala.topics.api.searchTopics
import ie.koala.topics.db.TopicLocalCache
import ie.koala.topics.model.TopicResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Repository class that works with local and remote movieLiveData sources.
 */
class TopicRepository(
        private val service: TopicService,
        private val cache: TopicLocalCache
) {

    /*
     * TMDB chunks responses in pages (which according to https://www.themoviedb.org/talk/587bea71c3a36846c300ff73)
     * are always sent back in blocks of 20
     *
     * This variable keeps a record of the last page requested.
     */
    private var page = 1    // This variable keeps a record of the last page requested.

    // LiveData of network errors.
    private val networkErrors = MutableLiveData<String>()

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    fun getTopics(request: String): TopicResult {
        page = 1
        requestAndSaveData(request)

        // Get topicLiveData from the local cache
        if (request.isBlank()) {
            val data = cache.allTopics()
            return TopicResult(data, networkErrors)
        } else {
            val data = cache.searchTopics(request)
            return TopicResult(data, networkErrors)
        }
    }

    fun requestMore(request: String) {
        requestAndSaveData(request)
    }

    private fun requestAndSaveData(request: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        if (request.isBlank()) {
            allTopics(service, BuildConfig.ApiKey, Locale.getDefault().toString(), page, { topics ->
                cache.insert(topics) {
                    page += 1
                    isRequestInProgress = false
                }
            }, { error ->
                networkErrors.postValue(error)
                isRequestInProgress = false
            })
        } else {
            searchTopics(service, request, BuildConfig.ApiKey, Locale.getDefault().toString(), page, { topics ->
                cache.insert(topics) {
                    page += 1
                    isRequestInProgress = false
                }
            }, { error ->
                networkErrors.postValue(error)
                isRequestInProgress = false
            })
        }
    }

    companion object {
        /*
         * According to https://www.themoviedb.org/talk/587bea71c3a36846c300ff73
         * allTopics are always sent back in blocks of 20
         *
         * Currently unused!
         */
        private const val PAGE_SIZE = 20

        val log: Logger = LoggerFactory.getLogger(TopicRepository::class.java)

    }
}