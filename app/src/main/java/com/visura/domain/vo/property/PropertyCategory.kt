package com.visura.domain.vo.property

import com.visura.domain.exceptions.property.PropertyException

enum class PropertyCategoryType(val displayName: String) {
    HOME("Casa"),
    APARTMENT("Apartamento")
}

@JvmInline
value class PropertyCategory(val value: PropertyCategoryType) {
    companion object {
        fun of(value: PropertyCategoryType?): Result<PropertyCategory> {
            return when (value) {
                null -> Result.failure(PropertyException.CategoryRequired())
                else -> Result.success(PropertyCategory(value))
            }
        }
    }
}