package com.visura.domain.usecase.evidence

import com.visura.domain.exceptions.evidence.PhotoEvidenceException
import com.visura.domain.repositories.evidence.PhotoEvidence
import com.visura.domain.repositories.evidence.PhotoEvidenceRepository
import com.visura.domain.vo.evidence.PhotoEvidenceId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotoEvidenceUseCase @Inject constructor(
    private val evidenceRepository: PhotoEvidenceRepository
) {
    suspend fun save(evidence: PhotoEvidence): PhotoEvidenceId {
        validate(evidence)
        return runCatching {
            evidenceRepository.save(evidence)
        }.getOrElse { throw PhotoEvidenceException.NetworkError(it) }
    }

    suspend fun findById(id: PhotoEvidenceId): PhotoEvidence {
        return runCatching {
            evidenceRepository.findById(id)
        }.getOrElse { throw PhotoEvidenceException.NotFound(it) }
    }

    fun listAll(): Flow<List<PhotoEvidence>> = evidenceRepository.listAll()

    fun listByChecklistItem(checklistItemId: String): Flow<List<PhotoEvidence>> =
        evidenceRepository.listByChecklistItem(checklistItemId)

    suspend fun delete(id: PhotoEvidenceId) {
        runCatching {
            evidenceRepository.delete(id)
        }.getOrElse { throw PhotoEvidenceException.NetworkError(it) }
    }

    private fun validate(evidence: PhotoEvidence) {
        if (evidence.checklistItemId.isBlank())
            throw PhotoEvidenceException.ChecklistItemRequired()
        if (evidence.fileUrl.isBlank())
            throw PhotoEvidenceException.UrlRequired()
    }
}