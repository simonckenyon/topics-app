package ie.koala.topics.feature.topic

interface TopicListener {
    fun onItemDeleted(topic: Topic)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}
