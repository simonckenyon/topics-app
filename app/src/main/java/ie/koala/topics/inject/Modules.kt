/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ie.koala.topics.inject

//import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.feature.stocks.config.AppExecutors
import ie.koala.topics.feature.stocks.repository.StockRepository
import ie.koala.topics.feature.stocks.repository.realtimeDatabase.RealtimeDatabaseStockRepository
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

interface RuntimeConfig {
    var stockRepository: StockRepository
}

class SingletonRuntimeConfig : RuntimeConfig {
    companion object {
        val instance = SingletonRuntimeConfig()
    }

    override var stockRepository: StockRepository = realtimeDatabaseStockRepository
}

private val realtimeDatabaseStockRepository by lazy { RealtimeDatabaseStockRepository() }

val appModule: Module = module {
    single { realtimeDatabaseStockRepository }
    single { SingletonRuntimeConfig.instance as RuntimeConfig }
    factory { get<RuntimeConfig>().stockRepository }
    single { AppExecutors.instance }
}

val firebaseModule: Module = module {
    single {
        val instance = FirebaseDatabase.getInstance()
        instance.setPersistenceEnabled(false)
        instance
    }
}

val allModules = listOf(appModule, firebaseModule)
