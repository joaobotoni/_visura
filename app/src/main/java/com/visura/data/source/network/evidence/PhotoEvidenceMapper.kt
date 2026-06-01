package com.visura.data.source.network.evidence

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.evidence.PhotoEvidence
import com.visura.domain.vo.evidence.PhotoEvidenceId
import java.time.LocalDateTime

fun PhotoEvidence.toMap(): Map<String, Any?> = mapOf(
    "checklistItemId" to checklistItemId,
    "fileUrl" to fileUrl,
    "capturedAt" to capturedAt.toString()
)

fun DocumentSnapshot.toPhotoEvidence(): PhotoEvidence = PhotoEvidence(
    id = PhotoEvidenceId(id),
    checklistItemId = getString("checklistItemId") ?: "",
    fileUrl = getString("fileUrl") ?: "",
    capturedAt = try {
        LocalDateTime.parse(getString("capturedAt") ?: LocalDateTime.now().toString())
    } catch (e: Exception) {
        LocalDateTime.now()
    }
)