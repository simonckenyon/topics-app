package ie.koala.topics.ui

import ie.koala.topics.model.Topic

interface TopicListener {
    fun onItemDeleted(topic: Topic)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}
