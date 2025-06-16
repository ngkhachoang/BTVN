package com.example.btvn_nkh.data.model

data class StyleDto(
    val _id: String,
    val name: String,
    val key: String
)

data class StyleTabDto(
    val _id: String,
    val name: String,
    val styles: List<StyleDto>
)