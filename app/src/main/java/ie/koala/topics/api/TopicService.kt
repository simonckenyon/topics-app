package ie.koala.topics.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ie.koala.topics.api.TopicService.Companion.log
import ie.koala.topics.model.Topic
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

fun allTopics(
    service: TopicService,
    key: String,
    language: String,
    page: Int,
    onSuccess: (topics: List<Topic>) -> Unit,
    onError: (error: String) -> Unit) {

    service.allTopics(key, language, page).enqueue(
        object : Callback<TopicResponse> {
            override fun onFailure(call: Call<TopicResponse>?, t: Throwable) {
                val message = t.message ?: "unknown error"
                log.error("topRatedTopics.onFailure: $message")
                onError(message)
            }

            override fun onResponse(call: Call<TopicResponse>, response: Response<TopicResponse>) {
                if (response.isSuccessful) {
                    val topics: List<Topic>? = response.body()?.topics
                    if (topics != null) {
                        onSuccess(topics)
                    } else {
                        onError("topRatedTopics is null")
                    }
                } else {
                    onError(response.errorBody()?.string() ?: "Unknown error")
                }
            }
        }
    )
}

fun searchTopics(
    service: TopicService,
    query: String,
    key: String,
    language: String,
    page: Int,
    onSuccess: (topics: List<Topic>) -> Unit,
    onError: (error: String) -> Unit) {

    service.searchTopics(query, key, language, page).enqueue(
        object : Callback<TopicResponse> {
            override fun onFailure(call: Call<TopicResponse>?, t: Throwable) {
                val message = t.message ?: "unknown error"
                log.error("topRatedTopics.onFailure: $message")
                onError(message)
            }

            override fun onResponse(call: Call<TopicResponse>, response: Response<TopicResponse>) {
                if (response.isSuccessful) {
                    val topics: List<Topic>? = response.body()?.topics
                    if (topics != null) {
                        onSuccess(topics)
                    } else {
                        onError("topRatedTopics is null")
                    }
                } else {
                    onError(response.errorBody()?.string() ?: "Unknown error")
                }
            }
        }
    )
}

interface TopicService {
    @GET("topic/top_rated")
    fun allTopics(@Query("api_key") key: String,
                       @Query("language") language: String,
                       @Query("page") page: Int): Call<TopicResponse>

    @GET("search/topic")
    fun searchTopics(@Query("query") query: String,
                     @Query("api_key") key: String,
                     @Query("language") language: String,
                     @Query("page") page: Int): Call<TopicResponse>

    companion object {
        val log: Logger = LoggerFactory.getLogger(TopicService::class.java)

        const val BASE_URL = "https://api.thetopicdb.org/3/"
        const val WEBSITE_BASE_URL = "https://www.thetopicdb.org/topic/"

        private const val CACHE_SIZE = (5 * 1024 * 1024).toLong()   // 5 Megabytes
        private const val MAX_AGE = 5                               // The maximum amount of time (5 seconds) that a resource will be considered fresh.
        private const val MAX_STALE = 60 * 60 * 24 * 7              // The app is willing to accept a response that has exceeded its expiration time by (1 week).

        fun hasNetwork(context: Context): Boolean? {
            var isConnected: Boolean? = false // Initial Value
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }

        fun create(context: Context): TopicService {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BODY   // was BASIC

            val myCache = Cache(context.cacheDir, CACHE_SIZE)

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .cache(myCache)
                .addInterceptor { chain ->
                    var request = chain.request()
                    request = if (hasNetwork(context)!!)
                        request.newBuilder().header("Cache-Control", "public, max-age=$MAX_AGE").build()
                    else
                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=$MAX_STALE").build()
                    chain.proceed(request)
                }
                .build()

            val moshi = Moshi
                    .Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(TopicService::class.java)
        }
    }
}