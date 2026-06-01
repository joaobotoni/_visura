package com.visura.domain.exceptions.realstate

sealed class RealStateException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : RealStateException("Imobiliária não encontrada", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : RealStateException(message ?: "Erro de validação", cause)

    class CompanyNameRequired(
        cause: Throwable? = null
    ) : RealStateException("A razão social é obrigatória", cause)

    class CnpjRequired(
        cause: Throwable? = null
    ) : RealStateException("O CNPJ é obrigatório", cause)

    class CreciRequired(
        cause: Throwable? = null
    ) : RealStateException("O CRECI é obrigatório", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : RealStateException("Erro de rede ao processar imobiliária", cause)

    class UnexpectedError(
        cause: Throwable
    ) : RealStateException("Ocorreu um erro inesperado", cause)
}