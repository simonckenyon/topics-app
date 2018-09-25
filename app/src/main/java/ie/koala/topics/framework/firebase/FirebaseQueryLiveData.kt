package ie.koala.topics.framework.firebase

import android.os.Handler

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

import androidx.lifecycle.LiveData
import org.slf4j.LoggerFactory

class FirebaseQueryLiveData : LiveData<DataSnapshot> {

    private var query: Query? = null
    private val listener = MyValueEventListener()

    private var listenerRemovePending = false
    private val handler = Handler()

    private val removeListener = Runnable {
        query!!.removeEventListener(listener)
        listenerRemovePending = false
    }

    constructor(query: Query) {
        this.query = query
    }

    constructor(ref: DatabaseReference) {
        this.query = ref
    }

    override fun onActive() {
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener)
        } else {
            query!!.addValueEventListener(listener)
        }
        listenerRemovePending = false
    }

    override fun onInactive() {
        // Remove listener after a delay
        handler.postDelayed(removeListener, TWO_SECOND_DELAY)
        listenerRemovePending = true
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            value = dataSnapshot
        }

        override fun onCancelled(databaseError: DatabaseError) {
            log.error( "Can't listen to query $query", databaseError.toException())
        }
    }

    companion object {
        private val TWO_SECOND_DELAY: Long = 2000
        private val log = LoggerFactory.getLogger(FirebaseQueryLiveData::class.java)
    }
}