package com.visura.domain.usecase.report

import com.visura.domain.exceptions.report.ReportException
import com.visura.domain.repositories.report.Report
import com.visura.domain.repositories.report.ReportRepository
import com.visura.domain.vo.report.ReportId
import com.visura.domain.vo.report.ReportStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class ReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend fun save(report: Report): ReportId {
        validate(report)
        return runCatching {
            reportRepository.save(report)
        }.getOrElse { throw ReportException.NetworkError(it) }
    }

    suspend fun findById(id: ReportId): Report {
        return runCatching {
            reportRepository.findById(id)
        }.getOrElse { throw ReportException.NotFound(it) }
    }

    fun listAll(): Flow<List<Report>> = reportRepository.listAll()

    fun listByInspection(inspectionId: String): Flow<List<Report>> =
        reportRepository.listByInspection(inspectionId)

    suspend fun delete(id: ReportId) {
        runCatching {
            reportRepository.delete(id)
        }.getOrElse { throw ReportException.NetworkError(it) }
    }

    suspend fun sign(report: Report): Report = report.copy(
        status = ReportStatus.SIGNED,
        updatedAt = LocalDateTime.now()
    )

    suspend fun deliver(report: Report): Report = report.copy(
        status = ReportStatus.DELIVERED,
        updatedAt = LocalDateTime.now()
    )

    private fun validate(report: Report) {
        if (report.inspectionId.isBlank())
            throw ReportException.InspectionRequired()
    }
}