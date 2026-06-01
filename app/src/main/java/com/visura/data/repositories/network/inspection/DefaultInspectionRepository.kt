package com.visura.data.repositories.network.inspection

import com.visura.data.source.network.inspection.InspectionRemoteDataSource
import com.visura.domain.repositories.inspection.Inspection
import com.visura.domain.repositories.inspection.InspectionRepository
import com.visura.domain.vo.inspection.InspectionId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultInspectionRepository @Inject constructor(
    private val remoteDataSource: InspectionRemoteDataSource
) : InspectionRepository {

    override suspend fun save(inspection: Inspection): InspectionId =
        remoteDataSource.save(inspection)

    override suspend fun findById(id: InspectionId): Inspection =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<Inspection>> =
        remoteDataSource.listAll()

    override suspend fun delete(id: InspectionId) =
        remoteDataSource.delete(id)
}