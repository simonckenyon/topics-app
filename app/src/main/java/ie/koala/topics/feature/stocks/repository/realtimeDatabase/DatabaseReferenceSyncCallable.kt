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

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import ie.koala.topics.feature.stocks.repository.StockRepository
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal class DatabaseReferenceSyncCallable(
    private val ref: DatabaseReference,
    private val timeout: Long,
    private val unit: TimeUnit
) : Callable<StockRepository.SyncResult>, ValueEventListener {

    companion object {
        private val log = LoggerFactory.getLogger(DatabaseReferenceSyncCallable::class.java)
    }

    private val latch = CountDownLatch(1)

    private var numSnapshotsReceived = 0
    private var snapshot: DataSnapshot? = null
    private var exception: Exception? = null

    override fun call(): StockRepository.SyncResult {
        ref.addValueEventListener(this)
        try {
            latch.await(timeout, unit)
            if (snapshot != null) {
                return when (numSnapshotsReceived) {
                    0 -> StockRepository.SyncResult.TIMEOUT
                    1 -> StockRepository.SyncResult.UNKNOWN
                    else -> StockRepository.SyncResult.SUCCESS
                }
            }

            return StockRepository.SyncResult.FAILURE
        }
        finally {
            ref.removeEventListener(this)
        }
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        if (snapshot.exists()) {
            log.debug("onDataChange: snapshot exists")
            // Cache state is uncertain, but if we receive more than one
            // snapshot, we can be relatively certain that the stock is
            // fresh from the server.
            this.snapshot = snapshot
            numSnapshotsReceived++
            if (numSnapshotsReceived > 1) {
                latch.countDown()
            }
        }
        else {
            log.debug("onDataChange: snapshot does not exist")
            latch.countDown()
        }
    }

    override fun onCancelled(error: DatabaseError) {
        exception = error.toException()
        log.debug("onCancelled: reference ${ref.key} sync failed", exception)
        latch.countDown()
    }

}
