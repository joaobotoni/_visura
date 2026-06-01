package com.visura.data.source.network.user

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.user.User
import com.visura.domain.vo.user.UserId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("users")

    suspend fun save(user: User): UserId {
        val doc = if (user.id.value.isEmpty())
            collection.document()
        else
            collection.document(user.id.value)

        doc.set(user.toMap()).await()
        return UserId(doc.id)
    }

    suspend fun findById(id: UserId): User {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toUser()
    }

    suspend fun findByEmail(email: String): User? {
        val snapshot = collection
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toUser()
    }

    fun listAll(): Flow<List<User>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toUser() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: UserId) {
        collection.document(id.value).delete().await()
    }
}