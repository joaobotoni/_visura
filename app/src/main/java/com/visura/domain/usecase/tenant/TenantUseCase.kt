package com.visura.domain.usecase.tenant

import com.visura.domain.exceptions.tenant.TenantException
import com.visura.domain.repositories.tenant.Tenant
import com.visura.domain.repositories.tenant.TenantRepository
import com.visura.domain.vo.tenant.TenantId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TenantUseCase @Inject constructor(
    private val tenantRepository: TenantRepository
) {
    suspend fun save(tenant: Tenant): TenantId {
        validate(tenant)
        return runCatching {
            tenantRepository.save(tenant)
        }.getOrElse { throw TenantException.NetworkError(it) }
    }

    suspend fun findById(id: TenantId): Tenant {
        return runCatching {
            tenantRepository.findById(id)
        }.getOrElse { throw TenantException.NotFound(it) }
    }

    fun listAll(): Flow<List<Tenant>> = tenantRepository.listAll()

    suspend fun delete(id: TenantId) {
        runCatching {
            tenantRepository.delete(id)
        }.getOrElse { throw TenantException.NetworkError(it) }
    }

    private fun validate(tenant: Tenant) {
        if (tenant.name.isBlank())
            throw TenantException.NameRequired()
        if (tenant.cpf.isBlank())
            throw TenantException.CpfRequired()
    }
}