package com.visura.data.source.network.environment

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.environment.Environment
import com.visura.domain.repositories.environment.EnvironmentItem
import com.visura.domain.vo.environment.EnvironmentId
import com.visura.domain.vo.environment.EnvironmentStatus
import com.visura.domain.vo.environment.EnvironmentType

fun Environment.toMap(): Map<String, Any?> = mapOf(
    "name" to name,
    "propertyId" to propertyId,
    "type" to type.name,
    "items" to items.map { item ->
        mapOf(
            "id" to item.id,
            "name" to item.name,
            "description" to item.description,
            "environmentId" to item.environmentId.value,
            "status" to item.status.name
        )
    }
)

@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toEnvironment(): Environment {
    val itemsRaw = get("items") as? List<Map<String, Any>> ?: emptyList()
    val items = itemsRaw.map { map ->
        EnvironmentItem(
            id = map["id"] as? String ?: "",
            name = map["name"] as? String ?: "",
            description = map["description"] as? String ?: "",
            environmentId = EnvironmentId(map["environmentId"] as? String ?: ""),
            status = EnvironmentStatus.valueOf(
                map["status"] as? String ?: EnvironmentStatus.ACTIVE.name
            )
        )
    }
    return Environment(
        id = EnvironmentId(id),
        name = getString("name") ?: "",
        propertyId = getString("propertyId") ?: "",
        type = EnvironmentType.valueOf(
            getString("type") ?: EnvironmentType.OTHER.name
        ),
        items = items
    )
}