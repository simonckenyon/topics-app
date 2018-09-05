package ie.koala.topics.app

import android.app.Application
import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.BuildConfig

open class TopicsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    val versionName: String
        get() = BuildConfig.VERSION_NAME

    val versionCode: String
        get() = BuildConfig.VERSION_CODE.toString()

    val versionBuildTimestamp: String
        get() = BuildConfig.BUILD_TIME

    val versionGitHash: String
        get() = BuildConfig.GIT_HASH

    companion object {
        var context: Context? = null

        fun getMyApplication(): TopicsApplication {
            return context as TopicsApplication
        }
    }
}