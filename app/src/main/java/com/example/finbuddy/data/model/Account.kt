package com.example.finbuddy.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: String? = null,
    val user_id: String? = null,
    val name: String,
    val type: String,
    val balance: Double,
    val created_at: String? = null
)
