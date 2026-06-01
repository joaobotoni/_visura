package com.visura.domain.repositories.checklist

import com.visura.domain.vo.checklist.ChecklistId

data class InspectionChecklist(
    val id: ChecklistId = ChecklistId(""),
    val inspectionId: String,
    val environmentId: String,
    val environmentName: String,
    val isDone: Boolean = false,
    val items: List<ChecklistItem> = emptyList()
)