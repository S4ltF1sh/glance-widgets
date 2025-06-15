package com.s4ltf1sh.glance_widgets.di

import com.s4ltf1sh.glance_widgets.network.PicsumService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrlRecipeQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiRequest

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @ApiRequest
    fun provideRetrofit(
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://picsum.photos")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun providesPicsumService(
        @ApiRequest retrofit: Retrofit
    ): PicsumService = retrofit.create(PicsumService::class.java)
}