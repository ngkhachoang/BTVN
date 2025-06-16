package com.example.btvn_nkh.data.api

import com.example.btvn_nkh.data.model.StyleTabDto
import retrofit2.http.GET

interface StyleApiService {
    @GET("category?project=techtrek&segmentValue=IN&styleType=imageToImage&isApp=true")
    suspend fun getStyles(): StyleResponse
}

data class StyleResponse(val data: Data)
data class Data(val items: List<StyleTabDto>)