package com.visura.domain.exceptions.environment

sealed class EnvironmentException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : EnvironmentException("Ambiente não encontrado", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : EnvironmentException(message ?: "Erro de validação", cause)

    class NameRequired(
        cause: Throwable? = null
    ) : EnvironmentException("O nome do ambiente é obrigatório", cause)

    class PropertyRequired(
        cause: Throwable? = null
    ) : EnvironmentException("O imóvel é obrigatório", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : EnvironmentException("Erro de rede ao processar ambiente", cause)

    class UnexpectedError(
        cause: Throwable
    ) : EnvironmentException("Ocorreu um erro inesperado", cause)
}