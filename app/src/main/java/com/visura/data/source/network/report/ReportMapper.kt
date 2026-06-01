package com.visura.data.source.network.report

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.report.DigitalSignature
import com.visura.domain.repositories.report.Report
import com.visura.domain.repositories.report.ReportSection
import com.visura.domain.vo.report.ReportId
import com.visura.domain.vo.report.ReportStatus
import java.time.LocalDateTime

fun Report.toMap(): Map<String, Any?> = mapOf(
    "inspectionId" to inspectionId,
    "status" to status.name,
    "pdfUrl" to pdfUrl,
    "generatedAt" to generatedAt?.toString(),
    "createdAt" to createdAt.toString(),
    "updatedAt" to updatedAt.toString(),
    "sections" to sections.map { section ->
        mapOf(
            "id" to section.id,
            "reportId" to section.reportId.value,
            "environmentName" to section.environmentName,
            "description" to section.description,
            "order" to section.order
        )
    },
    "signatures" to signatures.map { signature ->
        mapOf(
            "id" to signature.id,
            "reportId" to signature.reportId.value,
            "inspectorId" to signature.inspectorId,
            "ownerId" to signature.ownerId,
            "tenantId" to signature.tenantId,
            "hash" to signature.hash,
            "signedAt" to signature.signedAt.toString()
        )
    }
)

@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toReport(): Report {
    val reportId = ReportId(id)

    val sectionsRaw = get("sections") as? List<Map<String, Any>> ?: emptyList()
    val sections = sectionsRaw.map { map ->
        ReportSection(
            id = map["id"] as? String ?: "",
            reportId = ReportId(map["reportId"] as? String ?: ""),
            environmentName = map["environmentName"] as? String ?: "",
            description = map["description"] as? String ?: "",
            order = (map["order"] as? Long)?.toInt() ?: 0
        )
    }

    val signaturesRaw = get("signatures") as? List<Map<String, Any>> ?: emptyList()
    val signatures = signaturesRaw.map { map ->
        DigitalSignature(
            id = map["id"] as? String ?: "",
            reportId = ReportId(map["reportId"] as? String ?: ""),
            inspectorId = map["inspectorId"] as? String ?: "",
            ownerId = map["ownerId"] as? String,
            tenantId = map["tenantId"] as? String,
            hash = map["hash"] as? String ?: "",
            signedAt = try {
                LocalDateTime.parse(map["signedAt"] as? String ?: LocalDateTime.now().toString())
            } catch (e: Exception) {
                LocalDateTime.now()
            }
        )
    }

    return Report(
        id = reportId,
        inspectionId = getString("inspectionId") ?: "",
        status = ReportStatus.valueOf(
            getString("status") ?: ReportStatus.DRAFT.name
        ),
        pdfUrl = getString("pdfUrl") ?: "",
        generatedAt = getString("generatedAt")?.let {
            try { LocalDateTime.parse(it) } catch (e: Exception) { null }
        },
        createdAt = try {
            LocalDateTime.parse(getString("createdAt") ?: LocalDateTime.now().toString())
        } catch (e: Exception) { LocalDateTime.now() },
        updatedAt = try {
            LocalDateTime.parse(getString("updatedAt") ?: LocalDateTime.now().toString())
        } catch (e: Exception) { LocalDateTime.now() },
        sections = sections,
        signatures = signatures
    )
}