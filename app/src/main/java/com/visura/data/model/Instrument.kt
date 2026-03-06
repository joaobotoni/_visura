package com.visura.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Instrument(
    val id: Int? = null,
    val name: String
)