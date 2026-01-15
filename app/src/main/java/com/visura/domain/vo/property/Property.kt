package com.visura.domain.vo.property

import com.visura.domain.exceptions.property.PropertyException

enum class PropertyType(val displayName: String) {
    RESIDENTIAL("Residencial"),
    NON_RESIDENTIAL("Não Residencial"),
    COMMERCIAL("Comercial")
}

@JvmInline
value class Property(val value: PropertyType) {
    companion object {
        fun of(value: PropertyType?): Result<Property> {
            return when (value) {
                null -> Result.failure(PropertyException.PropertyTypeRequired())
                else -> Result.success(Property(value))
            }
        }
    }
}