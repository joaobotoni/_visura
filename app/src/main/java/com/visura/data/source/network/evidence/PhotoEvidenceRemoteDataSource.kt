package com.visura.data.source.network.evidence

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.evidence.PhotoEvidence
import com.visura.domain.vo.evidence.PhotoEvidenceId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PhotoEvidenceRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("evidences")

    suspend fun save(evidence: PhotoEvidence): PhotoEvidenceId {
        val doc = if (evidence.id.value.isEmpty())
            collection.document()
        else
            collection.document(evidence.id.value)

        doc.set(evidence.toMap()).await()
        return PhotoEvidenceId(doc.id)
    }

    suspend fun findById(id: PhotoEvidenceId): PhotoEvidence {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toPhotoEvidence()
    }

    fun listAll(): Flow<List<PhotoEvidence>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toPhotoEvidence() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    fun listByChecklistItem(checklistItemId: String): Flow<List<PhotoEvidence>> = callbackFlow {
        val listener = collection
            .whereEqualTo("checklistItemId", checklistItemId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toPhotoEvidence() } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: PhotoEvidenceId) {
        collection.document(id.value).delete().await()
    }
}