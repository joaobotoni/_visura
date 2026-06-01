package com.visura.domain.exceptions.feature

sealed class PropertyFeatureException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : PropertyFeatureException("Característica do imóvel não encontrada", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : PropertyFeatureException(message ?: "Erro de validação", cause)

    class PropertyRequired(
        cause: Throwable? = null
    ) : PropertyFeatureException("O imóvel é obrigatório", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : PropertyFeatureException("Erro de rede ao processar característica", cause)

    class UnexpectedError(
        cause: Throwable
    ) : PropertyFeatureException("Ocorreu um erro inesperado", cause)
}