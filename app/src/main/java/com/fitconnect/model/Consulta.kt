package com.fitconnect.model

data class Consulta(
    val id: Long,
    val dataDisponivel: String,
    val horaDisponivel: String,
    val statusHorario: String
)