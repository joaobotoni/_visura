package com.visura.data.repositories.network.evidence

import com.visura.data.source.network.evidence.PhotoEvidenceRemoteDataSource
import com.visura.domain.repositories.evidence.PhotoEvidence
import com.visura.domain.repositories.evidence.PhotoEvidenceRepository
import com.visura.domain.vo.evidence.PhotoEvidenceId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultPhotoEvidenceRepository @Inject constructor(
    private val remoteDataSource: PhotoEvidenceRemoteDataSource
) : PhotoEvidenceRepository {

    override suspend fun save(evidence: PhotoEvidence): PhotoEvidenceId =
        remoteDataSource.save(evidence)

    override suspend fun findById(id: PhotoEvidenceId): PhotoEvidence =
        remoteDataSource.findById(id)

    override fun listAll(): Flow<List<PhotoEvidence>> =
        remoteDataSource.listAll()

    override fun listByChecklistItem(checklistItemId: String): Flow<List<PhotoEvidence>> =
        remoteDataSource.listByChecklistItem(checklistItemId)

    override suspend fun delete(id: PhotoEvidenceId) =
        remoteDataSource.delete(id)
}