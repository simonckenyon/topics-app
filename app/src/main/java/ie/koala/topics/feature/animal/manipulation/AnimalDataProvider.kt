package ie.koala.topics.feature.animal.manipulation

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ie.koala.topics.feature.animal.model.Animal

class AnimalDataProvider {

    var animalDataFactory: AnimalDataFactory = AnimalDataFactory()
    private val PAGE_SIZE = 4

    fun getAnimals(): LiveData<PagedList<Animal>>? {
        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build()


        return LivePagedListBuilder(animalDataFactory, config)
                .setInitialLoadKey("")
                .build()
    }
}