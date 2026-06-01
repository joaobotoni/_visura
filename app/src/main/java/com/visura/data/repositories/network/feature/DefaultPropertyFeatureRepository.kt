package com.visura.data.repositories.network.feature

import com.visura.data.source.network.feature.PropertyFeatureRemoteDataSource
import com.visura.domain.repositories.feature.PropertyFeature
import com.visura.domain.repositories.feature.PropertyFeatureRepository
import com.visura.domain.vo.feature.PropertyFeatureId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultPropertyFeatureRepository @Inject constructor(
    private val remoteDataSource: PropertyFeatureRemoteDataSource
) : PropertyFeatureRepository {

    override suspend fun save(feature: PropertyFeature): PropertyFeatureId =
        remoteDataSource.save(feature)

    override suspend fun findById(id: PropertyFeatureId): PropertyFeature =
        remoteDataSource.findById(id)

    override suspend fun findByProperty(propertyId: String): PropertyFeature? =
        remoteDataSource.findByProperty(propertyId)

    override fun listAll(): Flow<List<PropertyFeature>> =
        remoteDataSource.listAll()

    override suspend fun delete(id: PropertyFeatureId) =
        remoteDataSource.delete(id)
}