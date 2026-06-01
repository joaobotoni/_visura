package com.visura.data.source.network.feature

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.feature.PropertyFeature
import com.visura.domain.vo.feature.PropertyFeatureId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PropertyFeatureRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("features")

    suspend fun save(feature: PropertyFeature): PropertyFeatureId {
        val doc = if (feature.id.value.isEmpty())
            collection.document()
        else
            collection.document(feature.id.value)

        doc.set(feature.toMap()).await()
        return PropertyFeatureId(doc.id)
    }

    suspend fun findById(id: PropertyFeatureId): PropertyFeature {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toPropertyFeature()
    }

    suspend fun findByProperty(propertyId: String): PropertyFeature? {
        val snapshot = collection
            .whereEqualTo("propertyId", propertyId)
            .limit(1)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toPropertyFeature()
    }

    fun listAll(): Flow<List<PropertyFeature>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toPropertyFeature() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: PropertyFeatureId) {
        collection.document(id.value).delete().await()
    }
}