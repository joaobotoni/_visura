package com.visura.domain.usecase.property

import com.visura.domain.exceptions.property.PropertyException
import com.visura.domain.repositories.property.PropertyRepository
import com.visura.domain.vo.property.Property
import com.visura.domain.vo.property.PropertyId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PropertyUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository
) {
    suspend fun save(property: Property): PropertyId {
        validate(property)
        return runCatching {
            propertyRepository.save(property)
        }.getOrElse { throw PropertyException.NetworkError(it) }
    }

    suspend fun findById(id: PropertyId): Property {
        return runCatching {
            propertyRepository.findById(id)
        }.getOrElse { throw PropertyException.NotFound(it) }
    }

    fun listAll(): Flow<List<Property>> = propertyRepository.listAll()

    suspend fun delete(id: PropertyId) {
        runCatching {
            propertyRepository.delete(id)
        }.getOrElse { throw PropertyException.NetworkError(it) }
    }

    private fun validate(property: Property) {
        if (property.type.displayName.isBlank())
            throw PropertyException.PropertyTypeRequired()
        if (property.category.displayName.isBlank())
            throw PropertyException.CategoryRequired()
        if (property.address.street.isBlank())
            throw PropertyException.AddressRequired()
    }
}