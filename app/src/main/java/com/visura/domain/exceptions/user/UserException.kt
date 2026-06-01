package com.visura.domain.exceptions.user

sealed class UserException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : UserException("Usuário não encontrado", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : UserException(message ?: "Erro de validação", cause)

    class EmailRequired(
        cause: Throwable? = null
    ) : UserException("O e-mail é obrigatório", cause)

    class PasswordRequired(
        cause: Throwable? = null
    ) : UserException("A senha é obrigatória", cause)

    class EmailAlreadyExists(
        cause: Throwable? = null
    ) : UserException("E-mail já cadastrado", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : UserException("Erro de rede ao processar usuário", cause)

    class UnexpectedError(
        cause: Throwable
    ) : UserException("Ocorreu um erro inesperado", cause)
}