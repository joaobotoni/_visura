package com.visura.domain.repositories.feature

import com.visura.domain.vo.feature.PropertyFeatureId

data class PropertyFeature(
    val id: PropertyFeatureId = PropertyFeatureId(""),
    val propertyId: String,
    val floors: Int = 0,
    val parkingSpaces: Int = 0,
    val finishingStandard: String = ""
)