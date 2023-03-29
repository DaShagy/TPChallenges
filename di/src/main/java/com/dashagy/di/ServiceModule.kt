package com.dashagy.di

import com.dashagy.data.service.ImageServiceImpl
import com.dashagy.data.service.MoviesServiceImpl
import com.dashagy.data.service.api.TheMovieDatabaseAPI
import com.dashagy.domain.service.ImageService
import com.dashagy.domain.service.MoviesService
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideMoviesService(api: TheMovieDatabaseAPI): MoviesService = MoviesServiceImpl(api)

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideImageService(storage: FirebaseStorage): ImageService = ImageServiceImpl(storage)
}