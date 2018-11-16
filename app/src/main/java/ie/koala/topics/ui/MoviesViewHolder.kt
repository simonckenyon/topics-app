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

package ie.koala.topics.ui

import android.app.ActivityOptions
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ie.koala.topics.R
import ie.koala.topics.activity.DetailActivity
import ie.koala.topics.model.Topic
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * View Holder for a [Topic] RecyclerView list item.
 */
class TopicsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val title = view.findViewById(R.id.title) as TextView
    private val content = view.findViewById(R.id.content) as TextView

    private var topic: Topic? = null

    init {
        view.setOnClickListener { v: View ->
            topic?.let { clickedTopic: Topic ->
                // display details about this topic
                val context = v.context
                log.debug("onItemClick: topic=${clickedTopic.title}")
                val intent = DetailActivity.newIntent(context, clickedTopic)
                val options = ActivityOptions.makeSceneTransitionAnimation(context as AppCompatActivity, title, "topicTitle")
                context.startActivity(intent, options.toBundle())
            }
        }
    }

    fun bind(topic: Topic?, position: Int) {
        if (topic == null) {
            log.error("bind: topic is empty?")
        } else {
            showTopic(itemView, topic)
        }
    }

    private fun showTopic(view: View, topic: Topic) {
        this.topic = topic

        title.text = topic.title
    }

    private fun getPosterUrl(posterPath: String): String {
        return POSTER_PREFIX + posterPath
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(TopicsViewHolder::class.java)

        private const val POSTER_PREFIX = "https://image.tmdb.org/t/p/w500/"

        fun create(parent: ViewGroup): TopicsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false)
            return TopicsViewHolder(view)
        }
    }
}