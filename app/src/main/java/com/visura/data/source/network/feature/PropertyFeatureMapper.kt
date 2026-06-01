package com.visura.data.source.network.feature

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.feature.PropertyFeature
import com.visura.domain.vo.feature.PropertyFeatureId

fun PropertyFeature.toMap(): Map<String, Any?> = mapOf(
    "propertyId" to propertyId,
    "floors" to floors,
    "parkingSpaces" to parkingSpaces,
    "finishingStandard" to finishingStandard
)

fun DocumentSnapshot.toPropertyFeature(): PropertyFeature = PropertyFeature(
    id = PropertyFeatureId(id),
    propertyId = getString("propertyId") ?: "",
    floors = getLong("floors")?.toInt() ?: 0,
    parkingSpaces = getLong("parkingSpaces")?.toInt() ?: 0,
    finishingStandard = getString("finishingStandard") ?: ""
)