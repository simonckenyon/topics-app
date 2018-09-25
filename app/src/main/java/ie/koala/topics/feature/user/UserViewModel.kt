package ie.koala.topics.feature.user

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.framework.firebase.FirebaseQueryLiveData

class UserViewModel : ViewModel() {

    private val liveData = FirebaseQueryLiveData(USERS_REF)
    private val userLiveData = MediatorLiveData<User>()

    val dataSnapshotLiveData: LiveData<DataSnapshot>
        @NonNull
        get() = liveData

    init {
        // Set up the MediatorLiveData to convert DataSnapshot objects into User objects
        userLiveData.addSource(liveData) { dataSnapshot ->
            if (dataSnapshot != null) {
                Thread(Runnable { userLiveData.postValue(dataSnapshot.getValue(User::class.java)) }).start()
            } else {
                userLiveData.setValue(null)
            }
        }
    }

    companion object {
        private val FIREBASE_USERS: String = "users"
        private val USERS_REF = FirebaseDatabase.getInstance().getReference(FIREBASE_USERS)
    }
}