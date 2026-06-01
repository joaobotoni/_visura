package com.visura.domain.exceptions.tenant

sealed class TenantException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : TenantException("Inquilino não encontrado", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : TenantException(message ?: "Erro de validação", cause)

    class NameRequired(
        cause: Throwable? = null
    ) : TenantException("O nome do inquilino é obrigatório", cause)

    class CpfRequired(
        cause: Throwable? = null
    ) : TenantException("O CPF do inquilino é obrigatório", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : TenantException("Erro de rede ao processar inquilino", cause)

    class UnexpectedError(
        cause: Throwable
    ) : TenantException("Ocorreu um erro inesperado", cause)
}