package com.visura.domain.usecase.inspection

import com.visura.domain.exceptions.inspection.InspectionException
import com.visura.domain.repositories.inspection.Inspection
import com.visura.domain.repositories.inspection.InspectionRepository
import com.visura.domain.vo.inspection.InspectionId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InspectionUseCase @Inject constructor(
    private val inspectionRepository: InspectionRepository
) {
    suspend fun save(inspection: Inspection): InspectionId {
        validate(inspection)
        return runCatching {
            inspectionRepository.save(inspection)
        }.getOrElse { throw InspectionException.NetworkError(it) }
    }

    suspend fun findById(id: InspectionId): Inspection {
        return runCatching {
            inspectionRepository.findById(id)
        }.getOrElse { throw InspectionException.NotFound(it) }
    }

    fun listAll(): Flow<List<Inspection>> = inspectionRepository.listAll()

    suspend fun delete(id: InspectionId) {
        runCatching {
            inspectionRepository.delete(id)
        }.getOrElse { throw InspectionException.NetworkError(it) }
    }

    private fun validate(inspection: Inspection) {
        if (inspection.propertyId.isBlank())
            throw InspectionException.PropertyRequired()
        if (inspection.inspectorId.isBlank())
            throw InspectionException.InspectorRequired()
    }
}