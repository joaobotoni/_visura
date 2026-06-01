package com.visura.data.source.network.owner

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.owner.Owner
import com.visura.domain.vo.owner.OwnerId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OwnerRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("owners")

    suspend fun save(owner: Owner): OwnerId {
        val doc = if (owner.id.value.isEmpty())
            collection.document()
        else
            collection.document(owner.id.value)

        doc.set(owner.toMap()).await()
        return OwnerId(doc.id)
    }

    suspend fun findById(id: OwnerId): Owner {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toOwner()
    }

    fun listAll(): Flow<List<Owner>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toOwner() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: OwnerId) {
        collection.document(id.value).delete().await()
    }
}