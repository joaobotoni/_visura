package com.visura.domain.repositories.property

import com.visura.domain.vo.property.Property
import com.visura.domain.vo.property.PropertyId
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    suspend fun save(property: Property): PropertyId
    suspend fun findById(id: PropertyId): Property
    fun listAll(): Flow<List<Property>>
    suspend fun delete(id: PropertyId)
}