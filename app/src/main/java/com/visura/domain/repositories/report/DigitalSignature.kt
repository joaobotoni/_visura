package com.visura.domain.repositories.report

import com.visura.domain.vo.report.ReportId
import java.time.LocalDateTime

data class DigitalSignature(
    val id: String = "",
    val reportId: ReportId,
    val inspectorId: String,
    val ownerId: String? = null,
    val tenantId: String? = null,
    val hash: String = "",
    val signedAt: LocalDateTime = LocalDateTime.now()
)