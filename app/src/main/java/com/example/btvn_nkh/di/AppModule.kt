package com.example.btvn_nkh.di

import com.example.btvn_nkh.data.api.StyleApiService
import com.example.btvn_nkh.data.repository.StyleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api-style-manager.apero.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideStyleApiService(retrofit: Retrofit): StyleApiService =
        retrofit.create(StyleApiService::class.java)

    @Provides
    @Singleton
    fun provideStyleRepository(api: StyleApiService): StyleRepository =
        StyleRepository(api)
} 