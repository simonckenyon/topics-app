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
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ie.koala.topics.model.Topic

/**
 * Room topicLiveData access object for accessing the [Topic] table.
 */
@Dao
interface TopicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(topics: List<Topic>)

    // Look for allTopics
    @Query("SELECT * FROM topic")
    fun allTopics(): LiveData<List<Topic>>


    // Search for topics with the query string in the title
    @Query("SELECT * FROM topic WHERE title LIKE :queryString ORDER BY title ASC")
    fun searchTopics(queryString: String): LiveData<List<Topic>>
}