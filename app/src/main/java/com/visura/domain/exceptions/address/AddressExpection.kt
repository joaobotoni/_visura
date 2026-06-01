package com.visura.domain.exceptions.address

sealed class AddressException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : AddressException("Endereço não encontrado", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : AddressException(message ?: "Erro de validação", cause)

    class CityRequired(
        cause: Throwable? = null
    ) : AddressException("A cidade é obrigatória", cause)

    class StateRequired(
        cause: Throwable? = null
    ) : AddressException("O estado é obrigatório", cause)

    class StreetRequired(
        cause: Throwable? = null
    ) : AddressException("O logradouro é obrigatório", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : AddressException("Erro de rede ao processar endereço", cause)

    class UnexpectedError(
        cause: Throwable
    ) : AddressException("Ocorreu um erro inesperado", cause)
}