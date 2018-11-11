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
package ie.koala.topics.inject

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import ie.koala.topics.viewmodel.ViewModelFactory
import ie.koala.topics.data.TopicRepository
import ie.koala.topics.api.TopicService
import ie.koala.topics.db.TopicDatabase
import ie.koala.topics.db.TopicLocalCache
import java.util.concurrent.Executors

object Injection {

    /**
     * Creates an instance of [TopicLocalCache] based on the database DAO.
     */
    private fun provideCache(context: Context): TopicLocalCache {
        val database = TopicDatabase.getInstance(context)
        return TopicLocalCache(database.topicDao(), Executors.newSingleThreadExecutor())
    }

    /**
     * Creates an instance of [TopicRepository] based on the [TopicService] and a
     * [TopicLocalCache]
     */
    private fun provideTopicRepository(context: Context): TopicRepository {
        return TopicRepository(TopicService.create(context), provideCache(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to getTopics a reference to
     * [ViewModel] objects.
     */
    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideTopicRepository(context))
    }

}