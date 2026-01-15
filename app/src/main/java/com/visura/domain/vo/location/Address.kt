package com.visura.domain.vo.location

import com.visura.domain.exceptions.location.LocationException

data class Address(
    val street: String,
    val number: String,
    val complement: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val latitude: Double?,
    val longitude: Double?
) : Comparable<Address> {

    companion object {
        fun of(
            street: String?,
            number: String?,
            complement: String?,
            neighborhood: String?,
            city: String?,
            state: String?,
            country: String?,
            postalCode: String?,
            latitude: Double? = null,
            longitude: Double? = null
        ): Result<Address> {
            return when {
                street.isNullOrBlank() || city.isNullOrBlank() || state.isNullOrBlank() || country.isNullOrBlank() -> {
                    Result.failure(LocationException.AddressNotFoundException())
                }

                else -> Result.success(
                    Address(
                        street = street.trim(),
                        number = number?.trim()?.ifBlank { "S/N" } ?: "S/N",
                        complement = complement?.trim().orEmpty(),
                        neighborhood = neighborhood?.trim().orEmpty(),
                        city = city.trim(),
                        state = state.trim(),
                        country = country.trim(),
                        postalCode = postalCode?.trim().orEmpty(),
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            }
        }
    }

    override fun compareTo(other: Address): Int {
        return compareValuesBy(
            this,
            other,
            { it.state },
            { it.city },
            { it.neighborhood },
            { it.street }
        )
    }
}