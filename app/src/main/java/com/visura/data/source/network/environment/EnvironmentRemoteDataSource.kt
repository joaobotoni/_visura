package com.visura.data.source.network.environment

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.environment.Environment
import com.visura.domain.vo.environment.EnvironmentId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EnvironmentRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("environments")

    suspend fun save(environment: Environment): EnvironmentId {
        val doc = if (environment.id.value.isEmpty())
            collection.document()
        else
            collection.document(environment.id.value)

        doc.set(environment.toMap()).await()
        return EnvironmentId(doc.id)
    }

    suspend fun findById(id: EnvironmentId): Environment {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toEnvironment()
    }

    fun listAll(): Flow<List<Environment>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toEnvironment() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    fun listByProperty(propertyId: String): Flow<List<Environment>> = callbackFlow {
        val listener = collection
            .whereEqualTo("propertyId", propertyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toEnvironment() } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: EnvironmentId) {
        collection.document(id.value).delete().await()
    }
}