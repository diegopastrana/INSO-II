package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String,
    @SerialName("name") val nombre: String,
    val email: String,
    @SerialName("picture") val avatar: String,
    val rol: String = "cliente"
)