package com.fitconnect.model

data class Consulta(
    val id: Long,
    val profissionalId: Long?,
    val alunoId: Long?,
    val dataDisponivel: String?,
    val horaDisponivel: String?,
    val descricao: String?,
    val statusHorario: String?
)