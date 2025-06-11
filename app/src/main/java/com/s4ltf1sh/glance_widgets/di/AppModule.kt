package com.s4ltf1sh.glance_widgets.di

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    @Named("ListStringAdapter")
    fun provideListStringAdapter(moshi: Moshi): JsonAdapter<List<String>> {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return moshi.adapter(type)
    }
}