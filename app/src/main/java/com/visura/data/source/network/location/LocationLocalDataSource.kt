package com.visura.data.source.network.location

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.visura.domain.exceptions.location.LocationException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private val geocoder: Geocoder by lazy {
        Geocoder(context, Locale.getDefault())
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private suspend fun fetchCurrentLastLocation(): Location? {
        return try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
        } catch (e: SecurityException) {
            throw LocationException.PermissionDenied(cause = e)
        } catch (e: Exception) {
            throw LocationException.Timeout(cause = e)
        }
    }

    private suspend fun fetchAddressFromLocation(location: Location): List<Address> {
        return try {
            suspendCoroutine { continuation ->
                geocoder.getFromLocation(location.latitude, location.longitude, 5) { addresses ->
                    continuation.resume(addresses)
                }
            }
        } catch (e: Exception) {
            throw LocationException.GeocodingFailed(cause = e)
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun fetchCurrentAddress(): List<Address> {
        val location = fetchCurrentLastLocation()
            ?: throw LocationException.LocationNotFound()
        val addresses = fetchAddressFromLocation(location)
        if (addresses.isEmpty()) {
            throw LocationException.AddressNotFound()
        }
        return addresses
    }

    suspend fun fetchAddressByName(query: String): List<Address> {
        return try {
            suspendCoroutine { continuation ->
                geocoder.getFromLocationName(query, 5) { addresses ->
                    continuation.resume(addresses)
                }
            }.also { addresses ->
                if (addresses.isEmpty()) throw LocationException.AddressNotFound()
            }
        } catch (e: LocationException) {
            throw e
        } catch (e: Exception) {
            throw LocationException.GeocodingFailed(cause = e)
        }
    }
}
