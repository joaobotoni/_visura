package com.visura.domain.repositories.report

import com.visura.domain.vo.report.ReportId

data class ReportSection(
    val id: String = "",
    val reportId: ReportId,
    val environmentName: String,
    val description: String = "",
    val order: Int = 0
)