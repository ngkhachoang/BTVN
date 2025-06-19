package com.example.btvn_nkh.di

import android.content.ContentResolver
import android.content.Context
import com.example.btvn_nkh.data.api.StyleApiService
import com.example.btvn_nkh.data.repository.StyleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStyleRepository(api: StyleApiService): StyleRepository =
        StyleRepository(api)

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver
} 