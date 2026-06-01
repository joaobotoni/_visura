package com.visura.domain.repositories.report

import com.visura.domain.vo.report.ReportId
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun save(report: Report): ReportId
    suspend fun findById(id: ReportId): Report
    fun listAll(): Flow<List<Report>>
    fun listByInspection(inspectionId: String): Flow<List<Report>>
    suspend fun delete(id: ReportId)
}