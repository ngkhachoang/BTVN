package com.example.btvn_nkh.ai_art.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AiArtApiService {

    @PUT
    suspend fun pushImageToServer(
        @Url url: String,
        @Body file: RequestBody
    ): Response<ResponseBody>

    @GET("/api/v5/image-ai/presigned-link")
    suspend fun getPreSignedLink(): Response<PreSignedLink>

    @POST("/api/v5/image-ai")
    suspend fun generateAiArt(@Body requestBody: AiArtRequest): Response<AiArtResponse>
}