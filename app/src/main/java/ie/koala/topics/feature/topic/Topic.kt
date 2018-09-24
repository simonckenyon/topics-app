package ie.koala.topics.feature.topic

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Topic(var id: String = "", var displayIndex: Int = -1, var title: String = "", var content: String = "")
    : Parcelable {

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
