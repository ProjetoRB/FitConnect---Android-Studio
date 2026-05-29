package com.fitconnect.model

// Horário retornado pelo backend
data class Horario(
    val id: Long,
    val dataDisponivel: String,
    val horaDisponivel: String,
    val statusHorario: String
)

// Body enviado para agendar
data class AgendamentoRequest(
    val alunoId: Long,
    val horarioId: Long
)