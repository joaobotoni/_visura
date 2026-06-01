package com.visura.ui.presenter.screens

import com.visura.domain.vo.location.Address

fun Address.toPrimaryString(): String {
    val base = listOf(street, number).filter { it.isNotBlank() }.joinToString(", ")
    return listOf(base, neighborhood)
        .filter { it.isNotBlank() }
        .joinToString(" - ")
        .ifEmpty { "Endereço não informado" }
}

fun Address.toCityStateString(): String? {
    return listOf(city, state)
        .filter { it.isNotBlank() }
        .joinToString(" — ")
        .takeIf { it.isNotBlank() }
}

fun Address.toPrimaryFormat(): String = toPrimaryString()

fun Address.toSecondaryFormat(): String = toCityStateString() ?: ""