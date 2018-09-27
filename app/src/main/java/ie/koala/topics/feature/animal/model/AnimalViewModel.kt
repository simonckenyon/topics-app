package ie.koala.topics.feature.animal.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import ie.koala.topics.feature.animal.manipulation.AnimalDataProvider

class AnimalViewModel : ViewModel() {
    private val provider: AnimalDataProvider? = AnimalDataProvider()

    fun getAnimals(): LiveData<PagedList<Animal>>? {
        return provider?.getAnimals()
    }
}