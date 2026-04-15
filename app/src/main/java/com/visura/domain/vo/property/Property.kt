package com.visura.domain.vo.property

import com.visura.domain.exceptions.property.PropertyException
import com.visura.domain.vo.location.Address
import java.util.UUID
import java.time.Instant

data class Property(
    val id: UUID,
    val type: PropertyType,
    val category: PropertyCategory,
    val address: Address,
    val created: Instant
) {
    companion object {
        fun of(
            type: PropertyType,
            category: PropertyCategory,
            address: Address,
        ): Result<Property> {
            return when {
                type.displayName.isBlank() -> Result.failure(PropertyException.PropertyTypeRequired())
                category.displayName.isBlank() -> Result.failure(PropertyException.CategoryRequired())
                else -> Result.success(
                    Property(
                        id = UUID.randomUUID(),
                        type = type,
                        category = category,
                        address = address,
                        created = Instant.now()
                    )
                )
            }
        }
    }
}