package com.visura.data.repositories.network.environment

import com.visura.data.source.network.environment.EnvironmentRemoteDataSource
import com.visura.domain.repositories.environment.Environment
import com.visura.domain.repositories.environment.EnvironmentRepository
import com.visura.domain.vo.environment.EnvironmentId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultEnvironmentRepository @Inject constructor(
    private val remoteDataSource: EnvironmentRemoteDataSource
) : EnvironmentRepository {

    override suspend fun save(environment: Environment): EnvironmentId =
        remoteDataSource.save(environment)

    override suspend fun findById(id: EnvironmentId): Environment =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<Environment>> =
        remoteDataSource.listAll()

    override fun listByProperty(propertyId: String): Flow<List<Environment>> =
        remoteDataSource.listByProperty(propertyId)

    override suspend fun delete(id: EnvironmentId) =
        remoteDataSource.delete(id)
}