package com.visura.data.source.network.tenant

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.tenant.Tenant
import com.visura.domain.vo.tenant.TenantId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TenantRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("tenants")

    suspend fun save(tenant: Tenant): TenantId {
        val doc = if (tenant.id.value.isEmpty())
            collection.document()
        else
            collection.document(tenant.id.value)

        doc.set(tenant.toMap()).await()
        return TenantId(doc.id)
    }

    suspend fun findById(id: TenantId): Tenant {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toTenant()
    }

    fun listAll(): Flow<List<Tenant>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toTenant() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: TenantId) {
        collection.document(id.value).delete().await()
    }
}