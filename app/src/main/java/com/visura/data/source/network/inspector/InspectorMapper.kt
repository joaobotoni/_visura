package com.visura.data.source.network.inspector

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.inspector.Inspector
import com.visura.domain.repositories.inspector.InspectorContact
import com.visura.domain.vo.inspector.InspectorContactType
import com.visura.domain.vo.inspector.InspectorId
import java.time.LocalDateTime

fun Inspector.toMap(): Map<String, Any?> = mapOf(
    "userId" to userId,
    "name" to name,
    "cpf" to cpf,
    "isActive" to isActive,
    "createdAt" to createdAt.toString(),
    "contacts" to contacts.map { contact ->
        mapOf(
            "id" to contact.id,
            "value" to contact.value,
            "isPrimary" to contact.isPrimary,
            "type" to contact.type.name
        )
    }
)

@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toInspector(): Inspector {
    val contactsRaw = get("contacts") as? List<Map<String, Any>> ?: emptyList()
    val contacts = contactsRaw.map { map ->
        InspectorContact(
            id = map["id"] as? String ?: "",
            value = map["value"] as? String ?: "",
            isPrimary = map["isPrimary"] as? Boolean ?: false,
            type = InspectorContactType.valueOf(
                map["type"] as? String ?: InspectorContactType.MOBILE.name
            )
        )
    }
    return Inspector(
        id = InspectorId(id),
        userId = getString("userId") ?: "",
        name = getString("name") ?: "",
        cpf = getString("cpf") ?: "",
        isActive = getBoolean("isActive") ?: true,
        createdAt = try {
            LocalDateTime.parse(getString("createdAt") ?: LocalDateTime.now().toString())
        } catch (e: Exception) {
            LocalDateTime.now()
        },
        contacts = contacts
    )
}