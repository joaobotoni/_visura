package com.visura.data.source.network.tenant

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.tenant.Tenant
import com.visura.domain.repositories.tenant.TenantContact
import com.visura.domain.vo.tenant.TenantContactType
import com.visura.domain.vo.tenant.TenantId

fun Tenant.toMap(): Map<String, Any?> = mapOf(
    "name" to name,
    "cpf" to cpf,
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
fun DocumentSnapshot.toTenant(): Tenant {
    val contactsRaw = get("contacts") as? List<Map<String, Any>> ?: emptyList()
    val contacts = contactsRaw.map { map ->
        TenantContact(
            id = map["id"] as? String ?: "",
            value = map["value"] as? String ?: "",
            isPrimary = map["isPrimary"] as? Boolean ?: false,
            type = TenantContactType.valueOf(
                map["type"] as? String ?: TenantContactType.MOBILE.name
            )
        )
    }
    return Tenant(
        id = TenantId(id),
        name = getString("name") ?: "",
        cpf = getString("cpf") ?: "",
        contacts = contacts
    )
}