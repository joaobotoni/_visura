package com.visura.data.source.network.property

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.vo.location.Address
import com.visura.domain.vo.property.Property
import com.visura.domain.vo.property.PropertyCategory
import com.visura.domain.vo.property.PropertyType
import java.time.Instant
import java.util.UUID

fun Property.toMap(): Map<String, Any?> = mapOf(
    "type" to type.name,
    "category" to category.name,
    "street" to address.street,
    "number" to address.number,
    "complement" to address.complement,
    "neighborhood" to address.neighborhood,
    "city" to address.city,
    "state" to address.state,
    "country" to address.country,
    "postalCode" to address.postalCode,
    "latitude" to address.latitude,
    "longitude" to address.longitude,
    "created" to created.toString()
)

fun DocumentSnapshot.toProperty(): Property = Property(
    id = runCatching { UUID.fromString(id) }.getOrElse { UUID.randomUUID() },
    type = PropertyType.valueOf(
        getString("type") ?: PropertyType.RESIDENTIAL.name
    ),
    category = PropertyCategory.valueOf(
        getString("category") ?: PropertyCategory.HOME.name
    ),
    address = Address(
        street = getString("street") ?: "",
        number = getString("number") ?: "",
        complement = getString("complement") ?: "",
        neighborhood = getString("neighborhood") ?: "",
        city = getString("city") ?: "",
        state = getString("state") ?: "",
        country = getString("country") ?: "",
        postalCode = getString("postalCode") ?: "",
        latitude = getDouble("latitude") ?: 0.0,
        longitude = getDouble("longitude") ?: 0.0
    ),
    created = runCatching {
        Instant.parse(getString("created"))
    }.getOrElse { Instant.now() }
)