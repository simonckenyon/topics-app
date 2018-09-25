package ie.koala.topics.app

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.BuildConfig

open class TopicsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    companion object {
        val versionName: String
            get() = BuildConfig.VERSION_NAME

        val versionCode: String
            get() = BuildConfig.VERSION_CODE.toString()

        val versionBuildTimestamp: String
            get() = BuildConfig.BUILD_TIME

        val versionGitHash: String
            get() = BuildConfig.GIT_HASH

    }
}