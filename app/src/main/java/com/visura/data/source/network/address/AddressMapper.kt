package com.visura.data.source.network.address

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.address.AddressEntity
import com.visura.domain.vo.address.AddressId

fun AddressEntity.toMap(): Map<String, Any?> = mapOf(
    "street" to street,
    "number" to number,
    "complement" to complement,
    "neighborhood" to neighborhood,
    "city" to city,
    "state" to state,
    "country" to country,
    "postalCode" to postalCode
)

fun DocumentSnapshot.toAddressEntity(): AddressEntity = AddressEntity(
    id = AddressId(id),
    street = getString("street") ?: "",
    number = getString("number") ?: "",
    complement = getString("complement") ?: "",
    neighborhood = getString("neighborhood") ?: "",
    city = getString("city") ?: "",
    state = getString("state") ?: "",
    country = getString("country") ?: "Brasil",
    postalCode = getString("postalCode") ?: ""
)