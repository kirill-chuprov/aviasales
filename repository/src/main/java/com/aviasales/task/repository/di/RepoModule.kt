package com.aviasales.task.repository.di

import android.content.Context.MODE_PRIVATE
import com.aviasales.task.repository.data.local.db.AviasalesDbProvider
import com.aviasales.task.repository.data.remote.api.AviasalesApiProvider
import com.aviasales.task.repository.data.remote.datasource.CitiesRemoteSource
import com.aviasales.task.repository.data.repositoryImpl.CitiesRepositoryImpl
import com.aviasales.task.repository.domain.datasource.CitiesDataSource
import com.aviasales.task.repository.domain.interactors.GetCitiesUseCase
import com.aviasales.task.repository.domain.repository.CitiesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val repoModule = module {

  single { AviasalesDbProvider.createDb(androidContext()).entityDao() }

  single { AviasalesApiProvider.createApi() }

  single { androidContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE) }

  single<CitiesDataSource>("remote") { CitiesRemoteSource(get()) }

  single<CitiesRepository> { CitiesRepositoryImpl(get("remote")) }

  factory { GetCitiesUseCase(get()) }

}