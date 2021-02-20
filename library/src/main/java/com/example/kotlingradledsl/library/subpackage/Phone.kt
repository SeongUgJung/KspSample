package com.example.kotlingradledsl.library.subpackage

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Phone(
    val mobile: String,
    val home: String,
    val office: String
)