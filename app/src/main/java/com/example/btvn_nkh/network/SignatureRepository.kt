package com.example.btvn_nkh.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignatureRepository @Inject constructor(
    private val signatureApiService: SignatureApiService
) {

    suspend fun testAuthentication(): Result<String> {
        return try {
            val response = signatureApiService.testAuthentication()

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.authenticated == true) {
                    // Update timestamp from server if available
                    body.timestamp?.let { timestamp ->
                        SignatureApiManager.setTimeStamp(timestamp)
                    }
                    Result.success("Authentication successful: ${body.message}")
                } else {
                    Result.failure(Exception("Authentication failed: ${body?.message ?: "Unknown error"}"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection error: ${e.message}"))
        }
    }
}