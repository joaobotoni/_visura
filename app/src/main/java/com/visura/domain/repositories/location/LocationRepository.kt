package com.visura.domain.repositories.location

import com.visura.domain.vo.location.Address

interface LocationRepository {
    suspend fun fetchCurrentAddress(): List<Address>
    suspend fun fetchAddressByName(query: String): List<Address>
}