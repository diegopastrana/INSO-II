package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val userId: String)