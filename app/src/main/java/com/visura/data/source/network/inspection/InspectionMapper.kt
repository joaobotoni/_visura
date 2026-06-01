package com.visura.data.source.network.inspection

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.inspection.Inspection
import com.visura.domain.vo.inspection.InspectionId
import com.visura.domain.vo.inspection.InspectionStatus
import com.visura.domain.vo.inspection.InspectionType
import java.time.LocalDateTime

fun Inspection.toMap(): Map<String, Any?> = mapOf(
    "propertyId" to propertyId,
    "inspectorId" to inspectorId,
    "inspectionType" to inspectionType.name,
    "inspectionStatus" to inspectionStatus.name,
    "realStateId" to realStateId,
    "ownerId" to ownerId,
    "tenantId" to tenantId,
    "scheduledDate" to scheduledDate.toString(),
    "generalObservation" to generalObservation,
    "doneOffline" to doneOffline,
    "createdAt" to createdAt.toString(),
    "updatedAt" to updatedAt.toString()
)

fun DocumentSnapshot.toInspection(): Inspection = Inspection(
    id = InspectionId(id),
    propertyId = getString("propertyId") ?: "",
    inspectorId = getString("inspectorId") ?: "",
    inspectionType = InspectionType.valueOf(
        getString("inspectionType") ?: InspectionType.TENANT_ENTRY.name
    ),
    inspectionStatus = InspectionStatus.valueOf(
        getString("inspectionStatus") ?: InspectionStatus.SCHEDULED.name
    ),
    realStateId = getString("realStateId"),
    ownerId = getString("ownerId"),
    tenantId = getString("tenantId"),
    scheduledDate = try {
        LocalDateTime.parse(getString("scheduledDate") ?: LocalDateTime.now().toString())
    } catch (e: Exception) {
        LocalDateTime.now()
    },
    generalObservation = getString("generalObservation") ?: "",
    doneOffline = getBoolean("doneOffline") ?: false,
    createdAt = try {
        LocalDateTime.parse(getString("createdAt") ?: LocalDateTime.now().toString())
    } catch (e: Exception) {
        LocalDateTime.now()
    },
    updatedAt = try {
        LocalDateTime.parse(getString("updatedAt") ?: LocalDateTime.now().toString())
    } catch (e: Exception) {
        LocalDateTime.now()
    }
)