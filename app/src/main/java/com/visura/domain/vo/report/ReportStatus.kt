package com.visura.domain.vo.report

enum class ReportStatus(val description: String) {
    DRAFT("Rascunho"),
    GENERATED("Gerado"),
    SIGNED("Assinado"),
    DELIVERED("Entregue")
}