package com.visura.data.source.network.report

import com.google.firebase.firestore.FirebaseFirestore
import com.visura.domain.repositories.report.Report
import com.visura.domain.vo.report.ReportId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReportRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("reports")

    suspend fun save(report: Report): ReportId {
        val doc = if (report.id.value.isEmpty())
            collection.document()
        else
            collection.document(report.id.value)

        doc.set(report.toMap()).await()
        return ReportId(doc.id)
    }

    suspend fun findById(id: ReportId): Report {
        val snapshot = collection.document(id.value).get().await()
        return snapshot.toReport()
    }

    fun listAll(): Flow<List<Report>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toReport() } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    fun listByInspection(inspectionId: String): Flow<List<Report>> = callbackFlow {
        val listener = collection
            .whereEqualTo("inspectionId", inspectionId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toReport() } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun delete(id: ReportId) {
        collection.document(id.value).delete().await()
    }
}