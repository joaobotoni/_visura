package com.visura.domain.repositories.report

import com.visura.domain.vo.report.ReportId
import com.visura.domain.vo.report.ReportStatus
import java.time.LocalDateTime

data class Report(
    val id: ReportId = ReportId(""),
    val inspectionId: String,
    val status: ReportStatus = ReportStatus.DRAFT,
    val pdfUrl: String = "",
    val generatedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val sections: List<ReportSection> = emptyList(),
    val signatures: List<DigitalSignature> = emptyList()
)