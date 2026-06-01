package com.visura.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.visura.domain.exceptions.property.PropertyException
import com.visura.domain.usecase.property.PropertyUseCase
import com.visura.domain.vo.location.Address
import com.visura.domain.vo.property.Property
import com.visura.domain.vo.property.PropertyCategory
import com.visura.domain.vo.property.PropertyId
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

data class PropertyState(
    val properties: List<Property> = emptyList(),
    val selectedType: PropertyType? = null,
    val selectedCategory: PropertyCategory? = null,
    val selectedAddress: Address? = null,
    val isLoading: Boolean = false
)

sealed interface PropertyEvent {
    data object SavedSuccessfully : PropertyEvent
    data object DeletedSuccessfully : PropertyEvent
    data class Error(val exception: PropertyException) : PropertyEvent
}

@HiltViewModel
class PropertyViewModel @Inject constructor(
    private val propertyUseCase: PropertyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PropertyState())
    val state = _state.asStateFlow()

    private val _event = Channel<PropertyEvent>()
    val event = _event.receiveAsFlow()

    init {
        listAll()
    }

    fun setType(type: PropertyType) {
        _state.update { it.copy(selectedType = type) }
    }

    fun setCategory(category: PropertyCategory) {
        _state.update { it.copy(selectedCategory = category) }
    }

    fun setAddress(address: Address?) {
        _state.update { it.copy(selectedAddress = address) }
    }

    fun save() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val property = Property(
                    id = UUID.randomUUID(),
                    type = _state.value.selectedType
                        ?: throw PropertyException.PropertyTypeRequired(),
                    category = _state.value.selectedCategory
                        ?: throw PropertyException.CategoryRequired(),
                    address = _state.value.selectedAddress
                        ?: throw PropertyException.AddressRequired(),
                    created = Instant.now()
                )
                propertyUseCase.save(property)
            }.fold(
                onSuccess = { _event.send(PropertyEvent.SavedSuccessfully) },
                onFailure = { _event.send(PropertyEvent.Error(it.toPropertyException())) }
            )
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun delete(id: PropertyId) {
        viewModelScope.launch {
            runCatching {
                propertyUseCase.delete(id)
            }.fold(
                onSuccess = { _event.send(PropertyEvent.DeletedSuccessfully) },
                onFailure = { _event.send(PropertyEvent.Error(it.toPropertyException())) }
            )
        }
    }

    private fun listAll() {
        viewModelScope.launch {
            propertyUseCase.listAll().collect { list ->
                _state.update { it.copy(properties = list) }
            }
        }
    }

    private fun Throwable.toPropertyException(): PropertyException = when (this) {
        is PropertyException -> this
        else -> PropertyException.UnexpectedError(this)
    }
}