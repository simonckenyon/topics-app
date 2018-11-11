package ie.koala.topics.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Entity
@Parcelize
data class Topic(
        @field:Json(name = "id") @PrimaryKey() var id: String = "",
        @field:Json(name = "topicType") var topicType: String = "",
        @field:Json(name = "parentId") var parentId: String = "",
        @field:Json(name = "displayIndex") var displayIndex: Int = -1,
        @field:Json(name = "title") var title: String = "",
        @field:Json(name = "content") var content: String = ""
): Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as Topic?
        return id == that?.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun compareToByDisplayIndex(other: Topic): Int {
        return displayIndex.compareTo(other.displayIndex)
    }

    companion object Factory {
        const val ARG_TOPIC: String = "ARG_TOPIC"
        const val ARG_TOPIC_COUNT: String = "ARG_TOPIC_COUNT"
    }
}

