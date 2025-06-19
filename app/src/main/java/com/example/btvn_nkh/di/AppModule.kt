package com.example.btvn_nkh.di

import android.content.ContentResolver
import android.content.Context
import com.example.btvn_nkh.ai_art.network.AiArtApiService
import com.example.btvn_nkh.ai_art.network.ArtGenerationService
import com.example.btvn_nkh.ai_art.network.ImageUploadManager
import com.example.btvn_nkh.ai_art.processing.ImageProcessor
import com.example.btvn_nkh.ai_art.storage.FileManager
import com.example.btvn_nkh.ai_art.usecase.AiArtUseCase
import com.example.btvn_nkh.data.api.StyleApiService
import com.example.btvn_nkh.data.repository.StyleRepository
import com.example.btvn_nkh.network.SignatureNetworkClient
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
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

    @Provides
    @Singleton
    fun provideSignatureNetworkClient(): SignatureNetworkClient =
        SignatureNetworkClient()

    @Provides
    @Singleton
    @Named("signature")
    fun provideSignatureRetrofit(signatureNetworkClient: SignatureNetworkClient): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("https://api-img-gen-wrapper.apero.vn")
            .client(signatureNetworkClient.createHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    @Provides
    @Singleton
    fun provideAiArtApiService(@Named("signature") retrofit: Retrofit): AiArtApiService =
        retrofit.create(AiArtApiService::class.java)

    @Provides
    @Singleton
    fun provideImageProcessor(@ApplicationContext context: Context): ImageProcessor =
        ImageProcessor(context)

    @Provides
    @Singleton
    fun provideImageUploadManager(aiArtApiService: AiArtApiService): ImageUploadManager =
        ImageUploadManager(aiArtApiService)

    @Provides
    @Singleton
    fun provideArtGenerationService(aiArtApiService: AiArtApiService): ArtGenerationService =
        ArtGenerationService(aiArtApiService)

    @Provides
    @Singleton
    fun provideFileManager(): FileManager = FileManager()

    @Provides
    @Singleton
    fun provideAiArtUseCase(
        imageProcessor: ImageProcessor,
        imageUploadManager: ImageUploadManager,
        artGenerationService: ArtGenerationService,
        fileManager: FileManager
    ): AiArtUseCase =
        AiArtUseCase(imageProcessor, imageUploadManager, artGenerationService, fileManager)
} 