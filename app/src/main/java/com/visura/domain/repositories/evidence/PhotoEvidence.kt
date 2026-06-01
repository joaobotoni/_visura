package com.visura.domain.repositories.evidence

import com.visura.domain.vo.evidence.PhotoEvidenceId
import java.time.LocalDateTime

data class PhotoEvidence(
    val id: PhotoEvidenceId = PhotoEvidenceId(""),
    val checklistItemId: String,
    val fileUrl: String,
    val capturedAt: LocalDateTime = LocalDateTime.now()
)