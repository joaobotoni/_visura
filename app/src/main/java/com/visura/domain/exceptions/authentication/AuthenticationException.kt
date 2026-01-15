package com.visura.domain.exceptions.authentication

sealed class AuthenticationException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : AuthenticationException(message as String, cause)

    class NetworkError(
        cause: Throwable? = null
    ) : AuthenticationException("Ocorreu um erro de rede", cause)

    class InvalidCredential(
        cause: Throwable? = null
    ) : AuthenticationException("Credenciais inválidas", cause)

    class UserNotFound(
        cause: Throwable? = null
    ) : AuthenticationException("Usuário não encontrado", cause)

    class EmailAlreadyInUse(
        cause: Throwable? = null
    ) : AuthenticationException("E-mail já registrado", cause)

    class WeakPassword(
        cause: Throwable? = null
    ) : AuthenticationException("A senha não atende aos requisitos", cause)

    class TooManyRequests(
        cause: Throwable? = null
    ) : AuthenticationException("Muitas tentativas de autenticação", cause)

    class UserDisabled(
        cause: Throwable? = null
    ) : AuthenticationException("A conta de usuário está desativada", cause)

    class SocialAuthenticationFailed(
        val provider: String,
        cause: Throwable? = null
    ) : AuthenticationException("Falha na autenticação social: $provider", cause)

    class NoAccountFound(
        val provider: String
    ) : AuthenticationException("Nenhuma conta encontrada para o provedor: $provider")

    class UserCancelled(
        cause: Throwable? = null
    ) : AuthenticationException("Operação cancelada pelo usuário", cause)

    class UnknownAuthError(
        cause: Throwable
    ) : AuthenticationException("Erro de autenticação desconhecido", cause)

    class UnexpectedError(
        cause: Throwable
    ) : AuthenticationException("Ocorreu um erro inesperado", cause)
}