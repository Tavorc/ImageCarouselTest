package com.example.imagecarouseltest.di

import com.example.imagecarouseltest.data.repository.ImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageRepositoryModule {

    @Provides
    @Singleton
    fun provideImageRepository(): ImageRepository {
        return ImageRepository()
    }
}