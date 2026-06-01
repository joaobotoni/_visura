package com.visura.domain.exceptions.evidence

sealed class PhotoEvidenceException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NotFound(
        cause: Throwable? = null
    ) : PhotoEvidenceException("Evidência fotográfica não encontrada", cause)

    class ValidationError(
        message: String?,
        cause: Throwable? = null
    ) : PhotoEvidenceException(message ?: "Erro de validação", cause)

    class ChecklistItemRequired(
        cause: Throwable? = null
    ) : PhotoEvidenceException("O item do checklist é obrigatório", cause)

    class UrlRequired(
        cause: Throwable? = null
    ) : PhotoEvidenceException("A URL do arquivo é obrigatória", cause)

    class NetworkError(
        cause: Throwable? = null
    ) : PhotoEvidenceException("Erro de rede ao processar evidência", cause)

    class UnexpectedError(
        cause: Throwable
    ) : PhotoEvidenceException("Ocorreu um erro inesperado", cause)
}