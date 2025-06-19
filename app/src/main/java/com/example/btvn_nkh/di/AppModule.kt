package com.example.btvn_nkh.di

import android.content.ContentResolver
import android.content.Context
import com.example.btvn_nkh.data.api.StyleApiService
import com.example.btvn_nkh.data.repository.StyleRepository
import com.example.btvn_nkh.network.SignatureApiService
import com.example.btvn_nkh.network.SignatureNetworkClient
import com.example.btvn_nkh.network.SignatureRepository
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
    fun provideSignatureApiService(@Named("signature") retrofit: Retrofit): SignatureApiService =
        retrofit.create(SignatureApiService::class.java)

    @Provides
    @Singleton
    fun provideSignatureRepository(signatureApiService: SignatureApiService): SignatureRepository =
        SignatureRepository(signatureApiService)
} 