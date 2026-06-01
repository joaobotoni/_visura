package com.visura.data.source.network.owner

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.owner.Owner
import com.visura.domain.repositories.owner.OwnerContact
import com.visura.domain.vo.owner.OwnerContactType
import com.visura.domain.vo.owner.OwnerId

fun Owner.toMap(): Map<String, Any?> = mapOf(
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
fun DocumentSnapshot.toOwner(): Owner {
    val contactsRaw = get("contacts") as? List<Map<String, Any>> ?: emptyList()
    val contacts = contactsRaw.map { map ->
        OwnerContact(
            id = map["id"] as? String ?: "",
            value = map["value"] as? String ?: "",
            isPrimary = map["isPrimary"] as? Boolean ?: false,
            type = OwnerContactType.valueOf(
                map["type"] as? String ?: OwnerContactType.MOBILE.name
            )
        )
    }
    return Owner(
        id = OwnerId(id),
        name = getString("name") ?: "",
        cpf = getString("cpf") ?: "",
        contacts = contacts
    )
}