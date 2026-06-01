package com.visura.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.visura.domain.exceptions.inspection.InspectionException
import com.visura.domain.repositories.inspection.Inspection
import com.visura.domain.repositories.inspection.InspectionRepository
import com.visura.domain.usecase.inspection.InspectionUseCase
import com.visura.domain.vo.inspection.InspectionId
import com.visura.domain.vo.inspection.InspectionStatus
import com.visura.domain.vo.inspection.InspectionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class InspectionState(
    val inspections: List<Inspection> = emptyList(),
    val propertyId: String = "",
    val inspectorId: String = "",
    val selectedType: InspectionType? = null,
    val selectedStatus: InspectionStatus = InspectionStatus.SCHEDULED,
    val scheduledDate: LocalDateTime = LocalDateTime.now(),
    val generalObservation: String = "",
    val isLoading: Boolean = false
)

sealed interface InspectionEvent {
    data object SavedSuccessfully : InspectionEvent
    data object DeletedSuccessfully : InspectionEvent
    data class Error(val exception: InspectionException) : InspectionEvent
}

@HiltViewModel
class InspectionViewModel @Inject constructor(
    private val inspectionUseCase: InspectionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(InspectionState())
    val state = _state.asStateFlow()

    private val _event = Channel<InspectionEvent>()
    val event = _event.receiveAsFlow()

    init {
        listAll()
    }

    fun setPropertyId(id: String) {
        _state.update { it.copy(propertyId = id) }
    }

    fun setInspectorId(id: String) {
        _state.update { it.copy(inspectorId = id) }
    }

    fun setType(type: InspectionType) {
        _state.update { it.copy(selectedType = type) }
    }

    fun setStatus(status: InspectionStatus) {
        _state.update { it.copy(selectedStatus = status) }
    }

    fun setObservation(observation: String) {
        _state.update { it.copy(generalObservation = observation) }
    }

    fun setScheduledDate(date: LocalDateTime) {
        _state.update { it.copy(scheduledDate = date) }
    }

    fun save() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val inspection = Inspection(
                    propertyId = _state.value.propertyId,
                    inspectorId = _state.value.inspectorId,
                    inspectionType = _state.value.selectedType
                        ?: throw InspectionException.TypeRequired(),
                    inspectionStatus = _state.value.selectedStatus,
                    scheduledDate = _state.value.scheduledDate,
                    generalObservation = _state.value.generalObservation
                )
                inspectionUseCase.save(inspection)
            }.fold(
                onSuccess = { _event.send(InspectionEvent.SavedSuccessfully) },
                onFailure = { _event.send(InspectionEvent.Error(it.toInspectionException())) }
            )
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun delete(id: InspectionId) {
        viewModelScope.launch {
            runCatching {
                inspectionUseCase.delete(id)
            }.fold(
                onSuccess = { _event.send(InspectionEvent.DeletedSuccessfully) },
                onFailure = { _event.send(InspectionEvent.Error(it.toInspectionException())) }
            )
        }
    }

    private fun listAll() {
        viewModelScope.launch {
            inspectionUseCase.listAll().collect { list ->
                _state.update { it.copy(inspections = list) }
            }
        }
    }

    private fun Throwable.toInspectionException(): InspectionException = when (this) {
        is InspectionException -> this
        else -> InspectionException.UnexpectedError(this)
    }
}