package com.visura.ui.viewmodels

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.visura.domain.exceptions.location.LocationException
import com.visura.domain.exceptions.property.PropertyException
import com.visura.domain.usecase.location.LocationUseCase
import com.visura.domain.usecase.property.PropertyUseCase
import com.visura.domain.vo.location.Address
import com.visura.domain.vo.property.Property
import com.visura.domain.vo.property.PropertyCategory
import com.visura.domain.vo.property.PropertyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

data class RegisterState(
    val addresses: Set<Address> = emptySet(),
    val selectedAddress: Address? = null,
    val selectedPropertyCategory: PropertyCategory? = null,
    val selectedPropertyType: PropertyType? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val isFetchingLocation: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface RegisterEvent {
    data object SavedSuccessfully : RegisterEvent
    data class LocationError(val exception: LocationException) : RegisterEvent
    data class PropertyError(val exception: PropertyException) : RegisterEvent
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val locationUseCase: LocationUseCase,
    private val propertyUseCase: PropertyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _event = Channel<RegisterEvent>()
    val event = _event.receiveAsFlow()

    fun setPropertyCategory(propertyCategory: PropertyCategory?) {
        _state.update { it.copy(selectedPropertyCategory = propertyCategory) }
    }

    fun setPropertyType(propertyType: PropertyType?) {
        _state.update { it.copy(selectedPropertyType = propertyType) }
    }

    fun setAddress(address: Address?) {
        _state.update { it.copy(selectedAddress = address) }
    }

    fun setSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.length < 3) {
            _state.update { it.copy(addresses = emptySet()) }
        } else {
            searchAddress(query)
        }
    }

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    fun fetchCurrentAddress() {
        viewModelScope.launch {
            _state.update { it.copy(isFetchingLocation = true) }
            try {
                performCurrentLocationFetch()
            } finally {
                _state.update { it.copy(isFetchingLocation = false) }
            }
        }
    }

    fun searchAddress(query: String) {
        if (query.length < 3) return
        viewModelScope.launch {
            _state.update { it.copy(isSearching = true) }
            try {
                performAddressSearch(query)
            } finally {
                _state.update { it.copy(isSearching = false) }
            }
        }
    }

    fun saveProperty() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val property = Property(
                    id = UUID.randomUUID(),
                    type = _state.value.selectedPropertyType
                        ?: throw PropertyException.PropertyTypeRequired(),
                    category = _state.value.selectedPropertyCategory
                        ?: throw PropertyException.CategoryRequired(),
                    address = _state.value.selectedAddress
                        ?: throw LocationException.ValidationError("Endereço não selecionado"),
                    created = Instant.now()
                )
                propertyUseCase.save(property)
            }.fold(
                onSuccess = {
                    resetForm()
                    _event.send(RegisterEvent.SavedSuccessfully)
                },
                onFailure = { _event.send(RegisterEvent.PropertyError(it.toPropertyException())) }
            )
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun resetForm() {
        _state.update {
            it.copy(
                selectedAddress = null,
                selectedPropertyType = null,
                selectedPropertyCategory = null,
                addresses = emptySet(),
                searchQuery = ""
            )
        }
    }

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    private suspend fun performCurrentLocationFetch() {
        runCatching {
            locationUseCase.fetchAddress()
        }.fold(
            onSuccess = { addresses ->
                _state.update { it.copy(addresses = addresses.toSet()) }
            },
            onFailure = { exception ->
                _event.send(RegisterEvent.LocationError(exception.toLocationException()))
            }
        )
    }

    private suspend fun performAddressSearch(query: String) {
        runCatching {
            locationUseCase.fetchAddressByName(query)
        }.fold(
            onSuccess = { addresses ->
                _state.update { it.copy(addresses = addresses.toSet()) }
            },
            onFailure = { exception ->
                _event.send(RegisterEvent.LocationError(exception.toLocationException()))
            }
        )
    }

    private fun Throwable.toLocationException(): LocationException = when (this) {
        is LocationException -> this
        else -> LocationException.NetworkError(cause = this)
    }

    private fun Throwable.toPropertyException(): PropertyException = when (this) {
        is PropertyException -> this
        is LocationException -> PropertyException.ValidationError(this.message)
        else -> PropertyException.UnexpectedError(this)
    }
}