package com.visura.data.source.network.checklist

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.checklist.InspectionChecklist
import com.visura.domain.vo.checklist.ChecklistId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChecklistRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("checklists")

    suspend fun save(checklist: InspectionChecklist): ChecklistId {
        val doc = if (checklist.id.value.isEmpty())
            collection.document()
        else
            collection.document(checklist.id.value)

        doc.set(checklist.toMap()).await()
        return ChecklistId(doc.id)
    }

    suspend fun findById(id: ChecklistId): InspectionChecklist {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toInspectionChecklist()
    }

    fun listAll(): Flow<List<InspectionChecklist>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toInspectionChecklist() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    fun listByInspection(inspectionId: String): Flow<List<InspectionChecklist>> = callbackFlow {
        val listener = collection
            .whereEqualTo("inspectionId", inspectionId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toInspectionChecklist() } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: ChecklistId) {
        collection.document(id.value).delete().await()
    }
}