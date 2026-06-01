package com.visura.data.source.network.inspector

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.inspector.Inspector
import com.visura.domain.vo.inspector.InspectorId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InspectorRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("inspectors")

    suspend fun save(inspector: Inspector): InspectorId {
        val doc = if (inspector.id.value.isEmpty())
            collection.document()
        else
            collection.document(inspector.id.value)

        doc.set(inspector.toMap()).await()
        return InspectorId(doc.id)
    }

    suspend fun findById(id: InspectorId): Inspector {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toInspector()
    }

    fun listAll(): Flow<List<Inspector>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toInspector() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: InspectorId) {
        collection.document(id.value).delete().await()
    }
}