package ie.koala.topics.app

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

open class TopicsApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}