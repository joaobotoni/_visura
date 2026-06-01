package com.visura.domain.repositories.tenant

import com.visura.domain.vo.tenant.TenantId
import kotlinx.coroutines.flow.Flow

interface TenantRepository {
    suspend fun save(tenant: Tenant): TenantId
    suspend fun findById(id: TenantId): Tenant
    fun listAll(): Flow<List<Tenant>>
    suspend fun delete(id: TenantId)
}