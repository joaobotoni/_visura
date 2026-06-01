package com.visura.domain.repositories.environment

import com.visura.domain.vo.environment.EnvironmentId
import com.visura.domain.vo.environment.EnvironmentStatus

data class EnvironmentItem(
    val id: String = "",
    val name: String,
    val description: String = "",
    val environmentId: EnvironmentId,
    val status: EnvironmentStatus = EnvironmentStatus.ACTIVE
)
