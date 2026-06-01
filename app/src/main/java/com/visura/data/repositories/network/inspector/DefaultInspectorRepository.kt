package com.visura.data.repositories.network.inspector

import com.visura.data.source.network.inspector.InspectorRemoteDataSource
import com.visura.domain.repositories.inspector.Inspector
import com.visura.domain.repositories.inspector.InspectorRepository
import com.visura.domain.vo.inspector.InspectorId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultInspectorRepository @Inject constructor(
    private val remoteDataSource: InspectorRemoteDataSource
) : InspectorRepository {

    override suspend fun save(inspector: Inspector): InspectorId =
        remoteDataSource.save(inspector)

    override suspend fun findById(id: InspectorId): Inspector =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<Inspector>> =
        remoteDataSource.listAll()

    override suspend fun delete(id: InspectorId) =
        remoteDataSource.delete(id)
}