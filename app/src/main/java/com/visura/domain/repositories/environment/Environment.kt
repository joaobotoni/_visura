package com.visura.domain.repositories.environment

import com.visura.domain.vo.environment.EnvironmentId
import com.visura.domain.vo.environment.EnvironmentType

data class Environment(
    val id: EnvironmentId = EnvironmentId(""),
    val name: String,
    val propertyId: String,
    val type: EnvironmentType,
    val items: List<EnvironmentItem> = emptyList()
)