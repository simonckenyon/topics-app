package ie.koala.topics.feature.topic

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Topic(var id: String = "", var index: Int = -1, var title: String = "", var content: String = "")
    : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as Topic?
        return id == that?.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun compareToByIndex(other: Topic): Int {
        return index.compareTo(other.index)
    }

    companion object Factory {
        const val ARG_TOPIC: String = "ARG_TOPIC"
    }
}
