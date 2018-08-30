package ie.koala.topics.feature.topic

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Topic(var id: String? = null, var title: String? = null, var content: String? = null) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as Topic?
        return id == that?.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    companion object Factory {
        fun create(): Topic = Topic()
    }
}
