package com.visura.domain.repositories.realstate

import com.visura.domain.vo.realstate.RealStateContactType

data class RealStateContact(
    val id: String = "",
    val value: String,
    val isPrimary: Boolean = false,
    val type: RealStateContactType
)