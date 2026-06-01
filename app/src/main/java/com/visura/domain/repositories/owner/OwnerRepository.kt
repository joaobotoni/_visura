package com.visura.domain.repositories.owner

import com.visura.domain.vo.owner.OwnerId
import kotlinx.coroutines.flow.Flow

interface OwnerRepository {
    suspend fun save(owner: Owner): OwnerId
    suspend fun findById(id: OwnerId): Owner
    fun listAll(): Flow<List<Owner>>
    suspend fun delete(id: OwnerId)
}