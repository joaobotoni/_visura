package com.visura.data.source.network.property

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.vo.property.Property
import com.visura.domain.vo.property.PropertyId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PropertyRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("properties")

    suspend fun save(property: Property): PropertyId {
        val doc = collection.document()
        doc.set(property.toMap()).await()
        return PropertyId(doc.id)
    }

    suspend fun findById(id: PropertyId): Property {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toProperty()
    }

    fun listAll(): Flow<List<Property>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toProperty() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: PropertyId) {
        collection.document(id.value).delete().await()
    }
}