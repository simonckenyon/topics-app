package ie.koala.topics.feature.animal.model

import ie.koala.topics.feature.animal.firebase.FirebaseObject

data class Animal(
        val title: String? = null,
        val content: String? = null
): FirebaseObject()