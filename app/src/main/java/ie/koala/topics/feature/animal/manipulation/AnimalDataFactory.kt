package ie.koala.topics.feature.animal.manipulation

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import ie.koala.topics.feature.animal.model.Animal

class AnimalDataFactory : DataSource.Factory<String, Animal>() {

    private var datasourceLiveData = MutableLiveData<AnimalDataSource>()

    override fun create(): AnimalDataSource {
        val dataSource = AnimalDataSource()
        datasourceLiveData.postValue(dataSource)
        return dataSource
    }
}