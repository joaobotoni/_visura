package com.visura.domain.repositories.realstate

import com.visura.domain.vo.realstate.RealStateId
import kotlinx.coroutines.flow.Flow

interface RealStateRepository {
    suspend fun save(realState: RealState): RealStateId
    suspend fun findById(id: RealStateId): RealState
    fun listAll(): Flow<List<RealState>>
    suspend fun delete(id: RealStateId)
}