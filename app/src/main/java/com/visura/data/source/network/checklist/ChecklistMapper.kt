package com.visura.data.source.network.checklist

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.checklist.ChecklistItem
import com.visura.domain.repositories.checklist.InspectionChecklist
import com.visura.domain.vo.checklist.ChecklistId
import com.visura.domain.vo.checklist.ConservationState
import com.visura.domain.vo.environment.EnvironmentId

fun InspectionChecklist.toMap(): Map<String, Any?> = mapOf(
    "inspectionId" to inspectionId,
    "environmentId" to environmentId,
    "environmentName" to environmentName,
    "isDone" to isDone,
    "items" to items.map { item ->
        mapOf(
            "id" to item.id,
            "checklistId" to item.checklistId.value,
            "environmentItemId" to item.environmentItemId.value,
            "conservationState" to item.conservationState.name,
            "itemName" to item.itemName,
            "note" to item.note
        )
    }
)

@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toInspectionChecklist(): InspectionChecklist {
    val itemsRaw = get("items") as? List<Map<String, Any>> ?: emptyList()
    val items = itemsRaw.map { map ->
        ChecklistItem(
            id = map["id"] as? String ?: "",
            checklistId = ChecklistId(map["checklistId"] as? String ?: ""),
            environmentItemId = EnvironmentId(map["environmentItemId"] as? String ?: ""),
            conservationState = ConservationState.valueOf(
                map["conservationState"] as? String ?: ConservationState.GOOD.name
            ),
            itemName = map["itemName"] as? String ?: "",
            note = map["note"] as? String ?: ""
        )
    }
    return InspectionChecklist(
        id = ChecklistId(id),
        inspectionId = getString("inspectionId") ?: "",
        environmentId = getString("environmentId") ?: "",
        environmentName = getString("environmentName") ?: "",
        isDone = getBoolean("isDone") ?: false,
        items = items
    )
}