package ie.koala.topics.feature.animal.manipulation

import androidx.paging.ItemKeyedDataSource
import ie.koala.topics.feature.animal.firebase.FirebaseManager
import ie.koala.topics.feature.animal.model.Animal
import io.reactivex.schedulers.Schedulers

class AnimalDataSource : ItemKeyedDataSource<String, Animal>() {

    init {
        FirebaseManager.getAnimalChangeSubject()?.observeOn(Schedulers.io())?.subscribeOn(Schedulers.computation())?.subscribe { _ ->
            invalidate()
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<Animal>) {
        FirebaseManager.getAnimals(params.requestedLoadSize).subscribe({ animals ->
            callback.onResult(animals)
        }, {})
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Animal>) {
        FirebaseManager.getAnimalsAfter(params.key, params.requestedLoadSize).subscribe({ animals ->
            callback.onResult(animals)
        }, {})
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Animal>) {
        FirebaseManager.getAnimalsBefore(params.key, params.requestedLoadSize).subscribe({ animals ->
            callback.onResult(animals)
        }, {})
    }

    override fun getKey(item: Animal): String {
        return item.objectKey ?: ""
    }
}