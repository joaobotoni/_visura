package com.visura.domain.repositories.owner

import com.visura.domain.vo.owner.OwnerId

data class Owner(
    val id: OwnerId = OwnerId(""),
    val name: String,
    val cpf: String,
    val contacts: List<OwnerContact> = emptyList()
)