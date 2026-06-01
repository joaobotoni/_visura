package com.visura.domain.repositories.address

import com.visura.domain.vo.address.AddressId

data class AddressEntity(
    val id: AddressId = AddressId(""),
    val street: String,
    val number: String = "",
    val complement: String = "",
    val neighborhood: String = "",
    val city: String,
    val state: String,
    val country: String = "Brasil",
    val postalCode: String = ""
)