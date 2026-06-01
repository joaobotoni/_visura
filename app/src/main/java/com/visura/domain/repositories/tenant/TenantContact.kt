package com.visura.domain.repositories.tenant

import com.visura.domain.vo.tenant.TenantContactType

data class TenantContact(
    val id: String = "",
    val value: String,
    val isPrimary: Boolean = false,
    val type: TenantContactType
)