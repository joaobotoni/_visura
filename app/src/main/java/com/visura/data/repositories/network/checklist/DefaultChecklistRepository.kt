package com.visura.data.repositories.network.checklist

import com.visura.data.source.network.checklist.ChecklistRemoteDataSource
import com.visura.domain.repositories.checklist.ChecklistRepository
import com.visura.domain.repositories.checklist.InspectionChecklist
import com.visura.domain.vo.checklist.ChecklistId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultChecklistRepository @Inject constructor(
    private val remoteDataSource: ChecklistRemoteDataSource
) : ChecklistRepository {

    override suspend fun save(checklist: InspectionChecklist): ChecklistId =
        remoteDataSource.save(checklist)

    override suspend fun findById(id: ChecklistId): InspectionChecklist =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<InspectionChecklist>> =
        remoteDataSource.listAll()

    override fun listByInspection(inspectionId: String): Flow<List<InspectionChecklist>> =
        remoteDataSource.listByInspection(inspectionId)

    override suspend fun delete(id: ChecklistId) =
        remoteDataSource.delete(id)
}