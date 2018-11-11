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

package ie.koala.topics.feature.stocks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
import ie.koala.topics.R
import ie.koala.topics.feature.stocks.viewmodel.PagedStockPricesViewModel
import org.slf4j.LoggerFactory

//import org.koin.android.ext.android.inject

class StocksActivity : AppCompatActivity() {

    //private val auth by inject<FirebaseAuth>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The model
        val stocksViewModel = ViewModelProviders.of(this).get(PagedStockPricesViewModel::class.java)

        // The root view/scaffolding
        setContentView(R.layout.activity_stocks)

        findViewById<RecyclerView>(R.id.stocks_list).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@StocksActivity)
            adapter = StocksPagedListAdapter(
                stocksViewModel,
                this@StocksActivity,
                ItemClickListener()
            )
        }
    }

    override fun onStart() {
        super.onStart()
        //auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        //auth.removeAuthStateListener(authStateListener)
    }

//    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
//        if (auth.currentUser == null) {
//            finish()
//        }
//    }

    private inner class ItemClickListener : StocksPagedListAdapter.ItemClickListener<StockViewHolder> {
        override fun onItemClick(holder: StockViewHolder) {
            log.debug("onItemClick: holder=$holder")
        }
    }


    companion object {
        private val log = LoggerFactory.getLogger(StocksActivity::class.java)
    }


}
