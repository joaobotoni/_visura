package com.visura.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.visura.domain.exceptions.inspector.InspectorException
import com.visura.domain.repositories.inspector.Inspector
import com.visura.domain.usecase.inspector.InspectorUseCase
import com.visura.domain.vo.inspector.InspectorId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InspectorState(
    val inspectors: List<Inspector> = emptyList(),
    val name: String = "",
    val cpf: String = "",
    val isLoading: Boolean = false
)

sealed interface InspectorEvent {
    data object SavedSuccessfully : InspectorEvent
    data object DeletedSuccessfully : InspectorEvent
    data class Error(val exception: InspectorException) : InspectorEvent
}

@HiltViewModel
class InspectorViewModel @Inject constructor(
    private val inspectorUseCase: InspectorUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(InspectorState())
    val state = _state.asStateFlow()

    private val _event = Channel<InspectorEvent>()
    val event = _event.receiveAsFlow()

    init {
        listAll()
    }

    fun setName(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun setCpf(cpf: String) {
        _state.update { it.copy(cpf = cpf) }
    }

    fun save() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                val inspector = Inspector(
                    name = _state.value.name,
                    cpf = _state.value.cpf
                )
                inspectorUseCase.save(inspector)
            }.fold(
                onSuccess = { _event.send(InspectorEvent.SavedSuccessfully) },
                onFailure = { _event.send(InspectorEvent.Error(it.toInspectorException())) }
            )
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun delete(id: InspectorId) {
        viewModelScope.launch {
            runCatching {
                inspectorUseCase.delete(id)
            }.fold(
                onSuccess = { _event.send(InspectorEvent.DeletedSuccessfully) },
                onFailure = { _event.send(InspectorEvent.Error(it.toInspectorException())) }
            )
        }
    }

    private fun listAll() {
        viewModelScope.launch {
            inspectorUseCase.listAll().collect { list ->
                _state.update { it.copy(inspectors = list) }
            }
        }
    }

    private fun Throwable.toInspectorException(): InspectorException = when (this) {
        is InspectorException -> this
        else -> InspectorException.UnexpectedError(this)
    }
}