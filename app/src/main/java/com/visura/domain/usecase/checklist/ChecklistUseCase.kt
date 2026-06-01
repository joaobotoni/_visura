package com.visura.domain.usecase.checklist

import com.visura.domain.exceptions.checklist.ChecklistException
import com.visura.domain.repositories.checklist.ChecklistRepository
import com.visura.domain.repositories.checklist.InspectionChecklist
import com.visura.domain.vo.checklist.ChecklistId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChecklistUseCase @Inject constructor(
    private val checklistRepository: ChecklistRepository
) {
    suspend fun save(checklist: InspectionChecklist): ChecklistId {
        validate(checklist)
        return runCatching {
            checklistRepository.save(checklist)
        }.getOrElse { throw ChecklistException.NetworkError(it) }
    }

    suspend fun findById(id: ChecklistId): InspectionChecklist {
        return runCatching {
            checklistRepository.findById(id)
        }.getOrElse { throw ChecklistException.NotFound(it) }
    }

    fun listAll(): Flow<List<InspectionChecklist>> = checklistRepository.listAll()

    fun listByInspection(inspectionId: String): Flow<List<InspectionChecklist>> =
        checklistRepository.listByInspection(inspectionId)

    suspend fun delete(id: ChecklistId) {
        runCatching {
            checklistRepository.delete(id)
        }.getOrElse { throw ChecklistException.NetworkError(it) }
    }

    private fun validate(checklist: InspectionChecklist) {
        if (checklist.inspectionId.isBlank())
            throw ChecklistException.InspectionRequired()
        if (checklist.environmentId.isBlank())
            throw ChecklistException.EnvironmentRequired()
    }
}