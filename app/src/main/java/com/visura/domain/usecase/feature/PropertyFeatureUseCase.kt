package com.visura.domain.usecase.feature

import com.visura.domain.exceptions.feature.PropertyFeatureException
import com.visura.domain.repositories.feature.PropertyFeature
import com.visura.domain.repositories.feature.PropertyFeatureRepository
import com.visura.domain.vo.feature.PropertyFeatureId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PropertyFeatureUseCase @Inject constructor(
    private val featureRepository: PropertyFeatureRepository
) {
    suspend fun save(feature: PropertyFeature): PropertyFeatureId {
        validate(feature)
        return runCatching {
            featureRepository.save(feature)
        }.getOrElse { throw PropertyFeatureException.NetworkError(it) }
    }

    suspend fun findById(id: PropertyFeatureId): PropertyFeature {
        return runCatching {
            featureRepository.findById(id)
        }.getOrElse { throw PropertyFeatureException.NotFound(it) }
    }

    suspend fun findByProperty(propertyId: String): PropertyFeature? =
        featureRepository.findByProperty(propertyId)

    fun listAll(): Flow<List<PropertyFeature>> = featureRepository.listAll()

    suspend fun delete(id: PropertyFeatureId) {
        runCatching {
            featureRepository.delete(id)
        }.getOrElse { throw PropertyFeatureException.NetworkError(it) }
    }

    private fun validate(feature: PropertyFeature) {
        if (feature.propertyId.isBlank())
            throw PropertyFeatureException.PropertyRequired()
    }
}