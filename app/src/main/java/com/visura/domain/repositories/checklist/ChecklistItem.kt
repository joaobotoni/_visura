package com.visura.domain.repositories.checklist

import com.visura.domain.vo.checklist.ChecklistId
import com.visura.domain.vo.checklist.ConservationState
import com.visura.domain.vo.environment.EnvironmentId

data class ChecklistItem(
    val id: String = "",
    val checklistId: ChecklistId,
    val environmentItemId: EnvironmentId,
    val conservationState: ConservationState,
    val itemName: String,
    val note: String = ""
)