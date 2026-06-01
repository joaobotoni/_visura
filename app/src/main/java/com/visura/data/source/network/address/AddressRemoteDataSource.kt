package com.visura.data.source.network.address

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.address.AddressEntity
import com.visura.domain.vo.address.AddressId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AddressRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("addresses")

    suspend fun save(address: AddressEntity): AddressId {
        val doc = if (address.id.value.isEmpty())
            collection.document()
        else
            collection.document(address.id.value)

        doc.set(address.toMap()).await()
        return AddressId(doc.id)
    }

    suspend fun findById(id: AddressId): AddressEntity {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toAddressEntity()
    }

    fun listAll(): Flow<List<AddressEntity>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toAddressEntity() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: AddressId) {
        collection.document(id.value).delete().await()
    }
}