package com.visura.data.repositories.network.address

import com.visura.data.source.network.address.AddressRemoteDataSource
import com.visura.domain.repositories.address.AddressEntity
import com.visura.domain.repositories.address.AddressRepository
import com.visura.domain.vo.address.AddressId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultAddressRepository @Inject constructor(
    private val remoteDataSource: AddressRemoteDataSource
) : AddressRepository {

    override suspend fun save(address: AddressEntity): AddressId =
        remoteDataSource.save(address)

    override suspend fun findById(id: AddressId): AddressEntity =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<AddressEntity>> =
        remoteDataSource.listAll()

    override suspend fun delete(id: AddressId) =
        remoteDataSource.delete(id)
}