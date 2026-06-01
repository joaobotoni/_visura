package com.visura.domain.exceptions.inspector

sealed class InspectorException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : InspectorException("Vistoriador não encontrado", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : InspectorException(message ?: "Erro de validação", cause)

    class NameRequired(
        cause: Throwable? = null
    ) : InspectorException("O nome do vistoriador é obrigatório", cause)

    class CpfRequired(
        cause: Throwable? = null
    ) : InspectorException("O CPF do vistoriador é obrigatório", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : InspectorException("Erro de rede ao processar vistoriador", cause)

    class UnexpectedError(
        cause: Throwable
    ) : InspectorException("Ocorreu um erro inesperado", cause)
}