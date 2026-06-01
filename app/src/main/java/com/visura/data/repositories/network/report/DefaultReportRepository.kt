package com.visura.data.repositories.network.report

import com.visura.data.source.network.report.ReportRemoteDataSource
import com.visura.domain.repositories.report.Report
import com.visura.domain.repositories.report.ReportRepository
import com.visura.domain.vo.report.ReportId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultReportRepository @Inject constructor(
    private val remoteDataSource: ReportRemoteDataSource
) : ReportRepository {

    override suspend fun save(report: Report): ReportId =
        remoteDataSource.save(report)

    override suspend fun findById(id: ReportId): Report =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<Report>> =
        remoteDataSource.listAll()

    override fun listByInspection(inspectionId: String): Flow<List<Report>> =
        remoteDataSource.listByInspection(inspectionId)

    override suspend fun delete(id: ReportId) =
        remoteDataSource.delete(id)
}