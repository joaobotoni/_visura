package com.visura.domain.usecase.realstate

import com.visura.domain.exceptions.realstate.RealStateException
import com.visura.domain.repositories.realstate.RealState
import com.visura.domain.repositories.realstate.RealStateRepository
import com.visura.domain.vo.realstate.RealStateId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RealStateUseCase @Inject constructor(
    private val realStateRepository: RealStateRepository
) {
    suspend fun save(realState: RealState): RealStateId {
        validate(realState)
        return runCatching {
            realStateRepository.save(realState)
        }.getOrElse { throw RealStateException.NetworkError(it) }
    }

    suspend fun findById(id: RealStateId): RealState {
        return runCatching {
            realStateRepository.findById(id)
        }.getOrElse { throw RealStateException.NotFound(it) }
    }

    fun listAll(): Flow<List<RealState>> = realStateRepository.listAll()

    suspend fun delete(id: RealStateId) {
        runCatching {
            realStateRepository.delete(id)
        }.getOrElse { throw RealStateException.NetworkError(it) }
    }

    private fun validate(realState: RealState) {
        if (realState.companyName.isBlank())
            throw RealStateException.CompanyNameRequired()
        if (realState.cnpj.isBlank())
            throw RealStateException.CnpjRequired()
        if (realState.creci.isBlank())
            throw RealStateException.CreciRequired()
    }
}