package com.visura.domain.repositories.tenant

import com.visura.domain.vo.tenant.TenantId

data class Tenant(
    val id: TenantId = TenantId(""),
    val name: String,
    val cpf: String,
    val contacts: List<TenantContact> = emptyList()
)