package com.visura.data.repositories.network.property

import com.visura.data.source.network.property.PropertyRemoteDataSource
import com.visura.domain.repositories.property.PropertyRepository
import com.visura.domain.vo.property.Property
import com.visura.domain.vo.property.PropertyId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultPropertyRepository @Inject constructor(
    private val remoteDataSource: PropertyRemoteDataSource
) : PropertyRepository {

    override suspend fun save(property: Property): PropertyId =
        remoteDataSource.save(property)

    override suspend fun findById(id: PropertyId): Property =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<Property>> =
        remoteDataSource.listAll()

    override suspend fun delete(id: PropertyId) =
        remoteDataSource.delete(id)
}