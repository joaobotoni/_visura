package com.visura.domain.exceptions.property

sealed class PropertyException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : PropertyException(message as String, cause)

    class PropertyTypeRequired(
        cause: Throwable? = null
    ) : PropertyException("O tipo de propriedade é obrigatório", cause)

    class CategoryRequired(
        cause: Throwable? = null
    ) : PropertyException("A categoria da propriedade é obrigatória", cause)

    class UnexpectedError(
        cause: Throwable
    ) : PropertyException("Ocorreu um erro inesperado", cause)

    class NotFound(
        cause: Throwable? = null
    ) : PropertyException("Imóvel não encontrado", cause)

    class AddressRequired(
        cause: Throwable? = null
    ) : PropertyException("O endereço do imóvel é obrigatório", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : PropertyException("Erro de rede ao processar imóvel", cause)
}