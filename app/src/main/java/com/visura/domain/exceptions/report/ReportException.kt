package com.visura.domain.exceptions.report

sealed class ReportException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : ReportException("Laudo não encontrado", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : ReportException(message ?: "Erro de validação", cause)

    class InspectionRequired(
        cause: Throwable? = null
    ) : ReportException("A vistoria é obrigatória", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : ReportException("Erro de rede ao processar laudo", cause)

    class UnexpectedError(
        cause: Throwable
    ) : ReportException("Ocorreu um erro inesperado", cause)
}