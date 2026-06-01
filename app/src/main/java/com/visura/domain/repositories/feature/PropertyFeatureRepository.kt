package com.visura.domain.repositories.feature

import com.visura.domain.vo.feature.PropertyFeatureId
import kotlinx.coroutines.flow.Flow

interface PropertyFeatureRepository {
    suspend fun save(feature: PropertyFeature): PropertyFeatureId
    suspend fun findById(id: PropertyFeatureId): PropertyFeature
    suspend fun findByProperty(propertyId: String): PropertyFeature?
    fun listAll(): Flow<List<PropertyFeature>>
    suspend fun delete(id: PropertyFeatureId)
}