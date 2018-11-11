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
package ie.koala.topics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ie.koala.topics.data.TopicRepository
import ie.koala.topics.model.Topic
import ie.koala.topics.model.TopicResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * ViewModel for the [MainActivity] activity.
 * The ViewModel works with the [TopicRepository] to getTopics the topicLiveData.
 */
class TopicViewModel(private val repository: TopicRepository) : ViewModel() {

    private val queryLiveData = MutableLiveData<String>()
    private val topicResult: LiveData<TopicResult> = Transformations.map(queryLiveData) { request: String -> repository.getTopics(request) }

    val topics: LiveData<List<Topic>> = Transformations.switchMap(topicResult) { topicResult: TopicResult ->
        topicResult.topicLiveData
    }
    val networkErrors: LiveData<String> = Transformations.switchMap(topicResult) { topicResult: TopicResult ->
        topicResult.networkErrors
    }

    fun getTopics(request: String) {
        queryLiveData.postValue(request)
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE >= totalItemCount) {
            val immutableQuery = lastQueryValue()
            if (immutableQuery != null) {
                repository.requestMore(immutableQuery)
            }
        }
    }

    fun lastQueryValue(): String? = queryLiveData.value

    override fun toString(): String {
        return "TopicViewModel(" +
                "repository=$repository," +
                "queryLiveData=$queryLiveData," +
                "topicResult=$topicResult," +
                "topRatedTopics=$topics," +
                "networkErrors=$networkErrors" +
                ")"
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(TopicViewModel::class.java)

        private const val VISIBLE = 5
    }
}