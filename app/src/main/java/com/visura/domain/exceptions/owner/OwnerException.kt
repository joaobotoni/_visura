package com.visura.domain.exceptions.owner

sealed class OwnerException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : OwnerException("Proprietário não encontrado", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : OwnerException(message ?: "Erro de validação", cause)

    class NameRequired(
        cause: Throwable? = null
    ) : OwnerException("O nome do proprietário é obrigatório", cause)

    class CpfRequired(
        cause: Throwable? = null
    ) : OwnerException("O CPF do proprietário é obrigatório", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : OwnerException("Erro de rede ao processar proprietário", cause)

    class UnexpectedError(
        cause: Throwable
    ) : OwnerException("Ocorreu um erro inesperado", cause)
}