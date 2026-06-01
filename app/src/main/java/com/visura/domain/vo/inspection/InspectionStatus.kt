package com.visura.domain.vo.inspection

enum class InspectionStatus(val description: String) {
    SCHEDULED("Agendada"),
    IN_PROGRESS("Em andamento"),
    COMPLETED("Concluída"),
    CANCELED("Cancelada")
}