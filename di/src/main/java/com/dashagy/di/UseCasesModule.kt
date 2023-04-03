package com.dashagy.di

import com.dashagy.domain.repository.MoviesRepository
import com.dashagy.domain.service.ImageService
import com.dashagy.domain.service.LocationService
import com.dashagy.domain.service.MoviesService
import com.dashagy.domain.useCases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCasesModule {

    @Provides
    @ViewModelScoped
    fun provideGetMovieByIdUseCase(repository: MoviesRepository, moviesService: MoviesService) =
        GetMovieByIdUseCase(repository, moviesService)

    @Provides
    @ViewModelScoped
    fun provideGetPopularMoviesUseCase(repository: MoviesRepository, moviesService: MoviesService) =
        GetPopularMoviesUseCase(repository, moviesService)

    @Provides
    @ViewModelScoped
    fun provideSearchMovieUseCase(repository: MoviesRepository, moviesService: MoviesService) =
        SearchMoviesUseCase(repository, moviesService)

    @Provides
    @ViewModelScoped
    fun provideUploadImageToServiceUseCase(imageService: ImageService) =
        UploadImageToServiceUseCase(imageService)

    @Provides
    @ViewModelScoped
    fun provideSaveLocationToServiceUseCase(locationService: LocationService) =
        SaveLocationToServiceUseCase(locationService)

}