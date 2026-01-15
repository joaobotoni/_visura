package com.visura.domain.exceptions.location

import com.visura.domain.exceptions.authentication.AuthenticationException

sealed class LocationException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : LocationException(message as String, cause)

    class LocationNotFound(
        cause: Throwable? = null
    ) : LocationException("Localização não disponível", cause)

    class AddressNotFound(
        cause: Throwable? = null
    ) : LocationException("Endereço não encontrado", cause)

    class PermissionDenied(
        cause: Throwable? = null
    ) : LocationException("Permissão de localização negada", cause)

    class ServiceDisabled(
        cause: Throwable? = null
    ) : LocationException("Serviço de localização desativado", cause)

    class Timeout(
        cause: Throwable? = null
    ) : LocationException("Tempo limite da solicitação de localização esgotado", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : LocationException("Erro de rede ao buscar localização", cause)

    class GeocodingFailed(
        cause: Throwable? = null
    ) : LocationException("Falha ao converter coordenadas para endereço", cause)

    class UnexpectedError(
        cause: Throwable
    ) : LocationException("Ocorreu um erro inesperado", cause)
}