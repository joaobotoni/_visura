package com.visura.domain.usecase.inspector

import com.visura.domain.exceptions.inspector.InspectorException
import com.visura.domain.repositories.inspector.Inspector
import com.visura.domain.repositories.inspector.InspectorRepository
import com.visura.domain.vo.inspector.InspectorId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InspectorUseCase @Inject constructor(
    private val inspectorRepository: InspectorRepository
) {
    suspend fun save(inspector: Inspector): InspectorId {
        validate(inspector)
        return runCatching {
            inspectorRepository.save(inspector)
        }.getOrElse { throw InspectorException.NetworkError(it) }
    }

    suspend fun findById(id: InspectorId): Inspector {
        return runCatching {
            inspectorRepository.findById(id)
        }.getOrElse { throw InspectorException.NotFound(it) }
    }

    fun listAll(): Flow<List<Inspector>> = inspectorRepository.listAll()

    suspend fun delete(id: InspectorId) {
        runCatching {
            inspectorRepository.delete(id)
        }.getOrElse { throw InspectorException.NetworkError(it) }
    }

    private fun validate(inspector: Inspector) {
        if (inspector.name.isBlank())
            throw InspectorException.NameRequired()
        if (inspector.cpf.isBlank())
            throw InspectorException.CpfRequired()
    }
}