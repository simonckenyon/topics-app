package ie.koala.topics.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ie.koala.topics.model.Topic

@JsonClass(generateAdapter = true)
data class TopicResponse(
    @field:Json(name = "page") var page: Long = 0,
    @field:Json(name = "results") var topics: List<Topic>? = listOf(),
    @field:Json(name = "total_pages") var totalPages: Long = 0,
    @field:Json(name = "total_results") var totalResults: Long = 0
)
