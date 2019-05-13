package com.aviasales.task.repository.di

import android.content.Context.MODE_PRIVATE
import com.aviasales.task.repository.data.local.datasource.EntityLocalSource
import com.aviasales.task.repository.data.local.db.AviasalesDbProvider
import com.aviasales.task.repository.data.remote.api.AviasalesApiProvider
import com.aviasales.task.repository.data.remote.datasource.EntityRemoteSource
import com.aviasales.task.repository.data.repositoryImpl.EntitiesRepositoryImpl
import com.aviasales.task.repository.domain.datasource.EntityDataSource
import com.aviasales.task.repository.domain.interactors.*
import com.aviasales.task.repository.domain.repository.EntitiesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val repoModule = module {

  single { AviasalesDbProvider.createDb(androidContext()).entityDao() }

  single { AviasalesApiProvider.createApi() }

  single { androidContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE) }
  single<EntityDataSource>("local") { EntityLocalSource(get()) }

  single<EntityDataSource>("remote") { EntityRemoteSource(get()) }

  single<EntitiesRepository> { EntitiesRepositoryImpl(get("local"), get("remote")) }

  factory { ObserveEntitiesUseCase(get()) }

}