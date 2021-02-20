package com.example.kotlingradledsl.library

import com.example.kotlingradledsl.library.subpackage.Phone
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Person(
    val name: String,
    val age: Int,
    val phone: Phone
)
