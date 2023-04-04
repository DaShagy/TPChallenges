package com.dashagy.tpchallenges.di

import android.content.Context
import com.dashagy.tpchallenges.location.LocationClient
import com.dashagy.tpchallenges.location.LocationClientImpl
import com.dashagy.tpchallenges.service.LocationAndroidService
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFusedLocationServices(@ApplicationContext context: Context): FusedLocationProviderClient = FusedLocationProviderClient(context)

    @Singleton
    @Provides
    fun provideLocationClient(
        @ApplicationContext context: Context,
        client: FusedLocationProviderClient
    ): LocationClient = LocationClientImpl(context, client)

}