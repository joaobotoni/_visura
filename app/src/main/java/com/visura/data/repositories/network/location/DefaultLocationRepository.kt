package com.visura.data.repositories.network.location

import android.Manifest
import androidx.annotation.RequiresPermission
import com.visura.data.source.network.location.LocationLocalDataSource
import com.visura.domain.repositories.location.LocationRepository
import com.visura.domain.vo.location.Address
import javax.inject.Inject
import android.location.Address as AndroidAddress

class DefaultLocationRepository @Inject constructor(
    private val locationDatasource: LocationLocalDataSource
) : LocationRepository {

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun fetchCurrentAddress(): List<Address> {
        val rawData = locationDatasource.fetchCurrentAddress()
        return rawData.mapNotNull { it.toDomain() }
    }

    override suspend fun fetchAddressByName(query: String): List<Address> {
        val rawData = locationDatasource.fetchAddressByName(query) // CORRIGIDO
        return rawData.mapNotNull { it.toDomain() }
    }

    private fun AndroidAddress.toDomain(): Address? {
        return Address.of(
            street = this.thoroughfare,
            number = this.subThoroughfare,
            complement = this.featureName,
            neighborhood = this.subLocality,
            city = this.locality ?: this.subAdminArea,
            state = this.adminArea,
            country = this.countryName,
            postalCode = this.postalCode,
            latitude = this.latitude,
            longitude = this.longitude
        ).getOrNull()
    }
}