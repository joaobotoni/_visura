package com.visura.domain.repositories.address

import com.visura.domain.vo.address.AddressId
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    suspend fun save(address: AddressEntity): AddressId
    suspend fun findById(id: AddressId): AddressEntity
    fun listAll(): Flow<List<AddressEntity>>
    suspend fun delete(id: AddressId)
}