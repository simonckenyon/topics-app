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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ie.koala.topics.model.Topic
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration


/**
 * Database schema that holds the topic
 */
@Database(
    entities = [Topic::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TopicDatabase : RoomDatabase() {

    abstract fun topicDao(): TopicDao

    companion object {

        @Volatile
        private var INSTANCE: TopicDatabase? = null

        fun getInstance(context: Context): TopicDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }


        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // need to drop a "not null constraint" but sqllite doesn't support that
                // so just recreate the database
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TopicDatabase::class.java, "topRatedTopics.db"
            )
                .fallbackToDestructiveMigration()
                //.addMigrations(MIGRATION_1_2)
                .build()
    }
}