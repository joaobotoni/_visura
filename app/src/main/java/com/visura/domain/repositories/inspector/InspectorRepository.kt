package com.visura.domain.repositories.inspector

import com.visura.domain.vo.inspector.InspectorId
import kotlinx.coroutines.flow.Flow

interface InspectorRepository {
    suspend fun save(inspector: Inspector): InspectorId
    suspend fun findById(id: InspectorId): Inspector
    fun listAll(): Flow<List<Inspector>>
    suspend fun delete(id: InspectorId)
}