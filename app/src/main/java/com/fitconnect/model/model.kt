package com.fitconnect.model

data class AtualizarStatusAgendaRequest(
    val horarioId: Long,
    val alunoId: Long,
    val status: String
)