package com.visura.domain.repositories.environment

import com.visura.domain.vo.environment.EnvironmentId
import kotlinx.coroutines.flow.Flow

interface EnvironmentRepository {
    suspend fun save(environment: Environment): EnvironmentId
    suspend fun findById(id: EnvironmentId): Environment
    fun listAll(): Flow<List<Environment>>
    fun listByProperty(propertyId: String): Flow<List<Environment>>
    suspend fun delete(id: EnvironmentId)
}