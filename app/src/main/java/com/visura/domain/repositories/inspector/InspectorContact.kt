package com.visura.domain.repositories.inspector

import com.visura.domain.vo.inspector.InspectorContactType

data class InspectorContact(
    val id: String = "",
    val value: String,
    val isPrimary: Boolean = false,
    val type: InspectorContactType
)