package com.visura.data.source.network.realstate

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.realstate.RealState
import com.visura.domain.vo.realstate.RealStateId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RealStateRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("realstates")

    suspend fun save(realState: RealState): RealStateId {
        val doc = if (realState.id.value.isEmpty())
            collection.document()
        else
            collection.document(realState.id.value)

        doc.set(realState.toMap()).await()
        return RealStateId(doc.id)
    }

    suspend fun findById(id: RealStateId): RealState {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toRealState()
    }

    fun listAll(): Flow<List<RealState>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toRealState() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: RealStateId) {
        collection.document(id.value).delete().await()
    }
}