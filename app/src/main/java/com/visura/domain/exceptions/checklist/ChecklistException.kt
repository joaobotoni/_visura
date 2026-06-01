package com.visura.domain.exceptions.checklist

sealed class ChecklistException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : ChecklistException("Checklist não encontrado", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : ChecklistException(message ?: "Erro de validação", cause)

    class InspectionRequired(
        cause: Throwable? = null
    ) : ChecklistException("A vistoria é obrigatória", cause)

    class EnvironmentRequired(
        cause: Throwable? = null
    ) : ChecklistException("O ambiente é obrigatório", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : ChecklistException("Erro de rede ao processar checklist", cause)

    class UnexpectedError(
        cause: Throwable
    ) : ChecklistException("Ocorreu um erro inesperado", cause)
}