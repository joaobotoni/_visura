package com.visura.domain.usecase.address

import com.visura.domain.exceptions.address.AddressException
import com.visura.domain.repositories.address.AddressEntity
import com.visura.domain.repositories.address.AddressRepository
import com.visura.domain.vo.address.AddressId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    suspend fun save(address: AddressEntity): AddressId {
        validate(address)
        return runCatching {
            addressRepository.save(address)
        }.getOrElse { throw AddressException.NetworkError(it) }
    }

    suspend fun findById(id: AddressId): AddressEntity {
        return runCatching {
            addressRepository.findById(id)
        }.getOrElse { throw AddressException.NotFound(it) }
    }

    fun listAll(): Flow<List<AddressEntity>> = addressRepository.listAll()

    suspend fun delete(id: AddressId) {
        runCatching {
            addressRepository.delete(id)
        }.getOrElse { throw AddressException.NetworkError(it) }
    }

    private fun validate(address: AddressEntity) {
        if (address.street.isBlank())
            throw AddressException.StreetRequired()
        if (address.city.isBlank())
            throw AddressException.CityRequired()
        if (address.state.isBlank())
            throw AddressException.StateRequired()
    }
}