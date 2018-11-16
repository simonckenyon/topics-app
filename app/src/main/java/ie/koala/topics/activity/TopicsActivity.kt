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
package ie.koala.topics.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.koala.topics.R
import ie.koala.topics.inject.Injection
import ie.koala.topics.model.Topic
import ie.koala.topics.ui.TopicsAdapter
import ie.koala.topics.viewmodel.TopicViewModel
import kotlinx.android.synthetic.main.activity_topics.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TopicsActivity : AppCompatActivity() {

    private lateinit var viewModel: TopicViewModel
    private val adapter = TopicsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupScrollListener()

        // getTopics the view model
        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this))
            .get(TopicViewModel::class.java)

        initAdapter()

        val query: String? = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: ""
        if (query != null) {
            viewModel.getTopics(query)
        } else {
            viewModel.getTopics("")
        }
        adapter.submitList(null)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_refresh -> consume {
            // for now scroll to top of list
            list.smoothScrollToPosition(0)
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastQueryValue())
    }

    private inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }

    private fun initAdapter() {
        list.adapter = adapter
        viewModel.topics.observe(this, Observer<List<Topic>> {topicList ->
            log.warn("observe: topicList=$topicList")
            if (topicList.isNotEmpty()) {
                adapter.submitList(topicList)
            } else {
                log.warn("observe: topicList is empty")
                list.setEmptyView(emptyView)
            }
        })
        viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(this, "Network error $it", Toast.LENGTH_LONG).show()
        })
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        // Verify the action and getTopics the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                viewModel.getTopics("%$query%")
                adapter.submitList(null)
            }
        }
    }

    private fun setupScrollListener() {
        val layoutManager = list.layoutManager as LinearLayoutManager
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }
        })
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(TopicsActivity::class.java)

        private const val LAST_SEARCH_QUERY: String = "last_search_query"
    }
}

