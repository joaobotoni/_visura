package com.visura.domain.usecase.environment

import com.visura.domain.exceptions.environment.EnvironmentException
import com.visura.domain.repositories.environment.Environment
import com.visura.domain.repositories.environment.EnvironmentRepository
import com.visura.domain.vo.environment.EnvironmentId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EnvironmentUseCase @Inject constructor(
    private val environmentRepository: EnvironmentRepository
) {
    suspend fun save(environment: Environment): EnvironmentId {
        validate(environment)
        return runCatching {
            environmentRepository.save(environment)
        }.getOrElse { throw EnvironmentException.NetworkError(it) }
    }

    suspend fun findById(id: EnvironmentId): Environment {
        return runCatching {
            environmentRepository.findById(id)
        }.getOrElse { throw EnvironmentException.NotFound(it) }
    }

    fun listAll(): Flow<List<Environment>> = environmentRepository.listAll()

    fun listByProperty(propertyId: String): Flow<List<Environment>> =
        environmentRepository.listByProperty(propertyId)

    suspend fun delete(id: EnvironmentId) {
        runCatching {
            environmentRepository.delete(id)
        }.getOrElse { throw EnvironmentException.NetworkError(it) }
    }

    private fun validate(environment: Environment) {
        if (environment.name.isBlank())
            throw EnvironmentException.NameRequired()
        if (environment.propertyId.isBlank())
            throw EnvironmentException.PropertyRequired()
    }
}