package com.visura.domain.repositories.inspector

import com.visura.domain.vo.inspector.InspectorId
import java.time.LocalDateTime

data class Inspector(
    val id: InspectorId = InspectorId(""),
    val userId: String = "",
    val name: String,
    val cpf: String,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val contacts: List<InspectorContact> = emptyList()
)