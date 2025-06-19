package com.example.btvn_nkh.ai_art.network

import com.google.gson.annotations.SerializedName

data class AiArtRequest(
    @SerializedName("file") val file: String,
    @SerializedName("styleId") val styleId: String? = null,
    @SerializedName("positivePrompt") val positivePrompt: String? = null,
    @SerializedName("negativePrompt") val negativePrompt: String? = null
)

data class AiArtResponse(
    @SerializedName("data") val data: AiArtResponseData
)

data class AiArtResponseData(
    @SerializedName("url") val url: String
)

data class PreSignedLink(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Link?,
    @SerializedName("timestamp") val timestamp: Long
)

data class Link(
    @SerializedName("url") val url: String,
    @SerializedName("path") val path: String
)