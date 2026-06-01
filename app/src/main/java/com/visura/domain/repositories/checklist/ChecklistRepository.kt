package com.visura.domain.repositories.checklist

import com.visura.domain.vo.checklist.ChecklistId
import kotlinx.coroutines.flow.Flow

interface ChecklistRepository {
    suspend fun save(checklist: InspectionChecklist): ChecklistId
    suspend fun findById(id: ChecklistId): InspectionChecklist
    fun listAll(): Flow<List<InspectionChecklist>>
    fun listByInspection(inspectionId: String): Flow<List<InspectionChecklist>>
    suspend fun delete(id: ChecklistId)
}