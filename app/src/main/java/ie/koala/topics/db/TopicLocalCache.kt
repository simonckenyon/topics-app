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
package ie.koala.topics.db

import androidx.lifecycle.LiveData
import ie.koala.topics.model.Topic
import java.util.concurrent.Executor

/**
 * Class that handles the DAO local topicLiveData source. This ensures that methods are triggered on the
 * correct executor.
 */
class TopicLocalCache(
        private val topicDao: TopicDao,
        private val ioExecutor: Executor
) {
    /**
     * Insert a topic into the database, on a background thread.
     */
    fun insert(topics: List<Topic>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            topicDao.insert(topics)
            insertFinished()
        }
    }

    /**
     * Request a LiveData<TopicsResponse> from the Dao
     */
    fun allTopics(): LiveData<List<Topic>> {
        val topicLiveData = topicDao.allTopics()
        return topicLiveData
    }

    /**
     * Request a LiveData<TopicsResponse> from the Dao
     */
    fun searchTopics(queryString: String): LiveData<List<Topic>> {
        val topicLiveData = topicDao.searchTopics(queryString)
        return topicLiveData
    }
}