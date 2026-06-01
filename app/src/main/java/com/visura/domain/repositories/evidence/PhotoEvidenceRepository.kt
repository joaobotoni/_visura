package com.visura.domain.repositories.evidence

import com.visura.domain.vo.evidence.PhotoEvidenceId
import kotlinx.coroutines.flow.Flow

interface PhotoEvidenceRepository {
    suspend fun save(evidence: PhotoEvidence): PhotoEvidenceId
    suspend fun findById(id: PhotoEvidenceId): PhotoEvidence
    fun listAll(): Flow<List<PhotoEvidence>>
    fun listByChecklistItem(checklistItemId: String): Flow<List<PhotoEvidence>>
    suspend fun delete(id: PhotoEvidenceId)
}