package com.visura.domain.vo.inspection

enum class InspectionType(val description: String) {
    TENANT_ENTRY("Entrada de inquilino"),
    EXIT("Saída"),
    PERIODIC("Periódica"),
    PRE_SALE("Pré-venda")
}