package com.visura.domain.repositories.owner

import com.visura.domain.vo.owner.OwnerContactType

data class OwnerContact(
    val id: String = "",
    val value: String,
    val isPrimary: Boolean = false,
    val type: OwnerContactType
)