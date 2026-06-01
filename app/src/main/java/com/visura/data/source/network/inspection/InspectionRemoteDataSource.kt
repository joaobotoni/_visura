package com.visura.data.source.network.inspection

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.inspection.Inspection
import com.visura.domain.vo.inspection.InspectionId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InspectionRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("inspections")

    suspend fun save(inspection: Inspection): InspectionId {
        val doc = if (inspection.id.value.isEmpty())
            collection.document()
        else
            collection.document(inspection.id.value)

        doc.set(inspection.toMap()).await()
        return InspectionId(doc.id)
    }

    suspend fun findById(id: InspectionId): Inspection {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toInspection()
    }

    fun listAll(): Flow<List<Inspection>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toInspection() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: InspectionId) {
        collection.document(id.value).delete().await()
    }
}