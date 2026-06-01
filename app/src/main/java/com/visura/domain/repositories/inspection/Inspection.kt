package com.visura.domain.repositories.inspection

import com.visura.domain.vo.inspection.InspectionId
import com.visura.domain.vo.inspection.InspectionStatus
import com.visura.domain.vo.inspection.InspectionType
import java.time.LocalDateTime

data class Inspection(
    val id: InspectionId = InspectionId(""),
    val propertyId: String,
    val inspectorId: String,
    val inspectionType: InspectionType,
    val inspectionStatus: InspectionStatus = InspectionStatus.SCHEDULED,
    val realStateId: String? = null,
    val ownerId: String? = null,
    val tenantId: String? = null,
    val scheduledDate: LocalDateTime,
    val generalObservation: String = "",
    val doneOffline: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)