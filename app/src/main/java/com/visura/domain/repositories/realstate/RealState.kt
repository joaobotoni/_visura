package com.visura.domain.repositories.realstate

import com.visura.domain.vo.realstate.RealStateId

data class RealState(
    val id: RealStateId = RealStateId(""),
    val companyName: String,
    val cnpj: String,
    val creci: String,
    val addressId: String = "",
    val contacts: List<RealStateContact> = emptyList()
)