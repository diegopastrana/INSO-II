package com.example.utils

import kotlinx.serialization.Serializable
import java.util.stream.IntStream

@Serializable
data class PageLinks(
    val prev: String?,
    val self: String,
    val next: String?,
    val last: String
)

@Serializable
data class PaginatedResponse<T>(
    val page: Int,
    val size: Int,
    val total: Long
)