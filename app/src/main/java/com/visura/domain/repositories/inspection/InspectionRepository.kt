package com.visura.domain.repositories.inspection

import com.visura.domain.vo.inspection.InspectionId
import kotlinx.coroutines.flow.Flow

interface InspectionRepository {
    suspend fun save(inspection: Inspection): InspectionId
    suspend fun findById(id: InspectionId): Inspection
    fun listAll(): Flow<List<Inspection>>
    suspend fun delete(id: InspectionId)
}