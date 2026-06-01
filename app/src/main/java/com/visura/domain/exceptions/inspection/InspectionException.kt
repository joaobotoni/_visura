package com.visura.domain.exceptions.inspection

sealed class InspectionException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : InspectionException("Vistoria não encontrada", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : InspectionException(message ?: "Erro de validação", cause)

    class PropertyRequired(
        cause: Throwable? = null
    ) : InspectionException("O imóvel é obrigatório", cause)

    class InspectorRequired(
        cause: Throwable? = null
    ) : InspectionException("O vistoriador é obrigatório", cause)

    class TypeRequired(
        cause: Throwable? = null
    ) : InspectionException("O tipo de vistoria é obrigatório", cause)

    class DateRequired(
        cause: Throwable? = null
    ) : InspectionException("A data de realização é obrigatória", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : InspectionException("Erro de rede ao processar vistoria", cause)

    class UnexpectedError(
        cause: Throwable
    ) : InspectionException("Ocorreu um erro inesperado", cause)
}