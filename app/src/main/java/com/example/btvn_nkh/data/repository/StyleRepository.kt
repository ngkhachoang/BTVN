package com.example.btvn_nkh.data.repository

import com.example.btvn_nkh.data.api.StyleApiService
import com.example.btvn_nkh.data.model.StyleTabDto

class StyleRepository(private val api: StyleApiService) {
    suspend fun getStyles(): List<StyleTabDto> {
        return api.getStyles().data.items
    }
}