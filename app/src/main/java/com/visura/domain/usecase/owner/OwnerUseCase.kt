package com.visura.domain.usecase.owner

import com.visura.domain.exceptions.owner.OwnerException
import com.visura.domain.repositories.owner.Owner
import com.visura.domain.repositories.owner.OwnerRepository
import com.visura.domain.vo.owner.OwnerId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OwnerUseCase @Inject constructor(
    private val ownerRepository: OwnerRepository
) {
    suspend fun save(owner: Owner): OwnerId {
        validate(owner)
        return runCatching {
            ownerRepository.save(owner)
        }.getOrElse { throw OwnerException.NetworkError(it) }
    }

    suspend fun findById(id: OwnerId): Owner {
        return runCatching {
            ownerRepository.findById(id)
        }.getOrElse { throw OwnerException.NotFound(it) }
    }

    fun listAll(): Flow<List<Owner>> = ownerRepository.listAll()

    suspend fun delete(id: OwnerId) {
        runCatching {
            ownerRepository.delete(id)
        }.getOrElse { throw OwnerException.NetworkError(it) }
    }

    private fun validate(owner: Owner) {
        if (owner.name.isBlank())
            throw OwnerException.NameRequired()
        if (owner.cpf.isBlank())
            throw OwnerException.CpfRequired()
    }
}