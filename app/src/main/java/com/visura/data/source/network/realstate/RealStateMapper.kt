package com.visura.data.source.network.realstate

import com.google.firebase.firestore.DocumentSnapshot
import com.visura.domain.repositories.realstate.RealState
import com.visura.domain.repositories.realstate.RealStateContact
import com.visura.domain.vo.realstate.RealStateContactType
import com.visura.domain.vo.realstate.RealStateId

fun RealState.toMap(): Map<String, Any?> = mapOf(
    "companyName" to companyName,
    "cnpj" to cnpj,
    "creci" to creci,
    "addressId" to addressId,
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
fun DocumentSnapshot.toRealState(): RealState {
    val contactsRaw = get("contacts") as? List<Map<String, Any>> ?: emptyList()
    val contacts = contactsRaw.map { map ->
        RealStateContact(
            id = map["id"] as? String ?: "",
            value = map["value"] as? String ?: "",
            isPrimary = map["isPrimary"] as? Boolean ?: false,
            type = RealStateContactType.valueOf(
                map["type"] as? String ?: RealStateContactType.MOBILE.name
            )
        )
    }
    return RealState(
        id = RealStateId(id),
        companyName = getString("companyName") ?: "",
        cnpj = getString("cnpj") ?: "",
        creci = getString("creci") ?: "",
        addressId = getString("addressId") ?: "",
        contacts = contacts
    )
}