package com.visura.data.repositories.network.tenant

import com.visura.data.source.network.tenant.TenantRemoteDataSource
import com.visura.domain.repositories.tenant.Tenant
import com.visura.domain.repositories.tenant.TenantRepository
import com.visura.domain.vo.tenant.TenantId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultTenantRepository @Inject constructor(
    private val remoteDataSource: TenantRemoteDataSource
) : TenantRepository {

    override suspend fun save(tenant: Tenant): TenantId =
        remoteDataSource.save(tenant)

    override suspend fun findById(id: TenantId): Tenant =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<Tenant>> =
        remoteDataSource.listAll()

    override suspend fun delete(id: TenantId) =
        remoteDataSource.delete(id)
}