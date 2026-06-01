package com.visura.data.repositories.network.realstate

import com.visura.data.source.network.realstate.RealStateRemoteDataSource
import com.visura.domain.repositories.realstate.RealState
import com.visura.domain.repositories.realstate.RealStateRepository
import com.visura.domain.vo.realstate.RealStateId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultRealStateRepository @Inject constructor(
    private val remoteDataSource: RealStateRemoteDataSource
) : RealStateRepository {

    override suspend fun save(realState: RealState): RealStateId =
        remoteDataSource.save(realState)

    override suspend fun findById(id: RealStateId): RealState =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<RealState>> =
        remoteDataSource.listAll()

    override suspend fun delete(id: RealStateId) =
        remoteDataSource.delete(id)
}