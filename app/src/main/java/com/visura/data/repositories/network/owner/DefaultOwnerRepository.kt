package com.visura.data.repositories.network.owner

import com.visura.data.source.network.owner.OwnerRemoteDataSource
import com.visura.domain.repositories.owner.Owner
import com.visura.domain.repositories.owner.OwnerRepository
import com.visura.domain.vo.owner.OwnerId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultOwnerRepository @Inject constructor(
    private val remoteDataSource: OwnerRemoteDataSource
) : OwnerRepository {

    override suspend fun save(owner: Owner): OwnerId =
        remoteDataSource.save(owner)

    override suspend fun findById(id: OwnerId): Owner =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<Owner>> =
        remoteDataSource.listAll()

    override suspend fun delete(id: OwnerId) =
        remoteDataSource.delete(id)
}