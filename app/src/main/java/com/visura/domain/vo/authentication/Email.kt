package com.visura.domain.vo.authentication

import com.visura.domain.exceptions.authentication.AuthenticationException

@JvmInline
value class Email(val value: String) : Comparable<Email> {

    companion object {
        private val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        fun of(value: String): Result<Email> {
            return when {
                value.isBlank() -> Result.failure(
                    AuthenticationException.ValidationError("E-mail não pode estar vazio")
                )

                !(regex.matches(value)) -> Result.failure(
                    AuthenticationException.ValidationError("Formato de e-mail inválido")
                )

                else -> Result.success(Email(value.trim().lowercase()))
            }
        }
    }
    override fun compareTo(other: Email): Int = value.compareTo(other.value)
}