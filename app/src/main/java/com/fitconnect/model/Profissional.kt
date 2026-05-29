package com.fitconnect.model

data class Profissional(
    val id: Long,
    val nomeCompleto: String?,
    val email: String?,
    val cpf: String?,
    val areaProfissional: String?,
    val documentoProfissional: String?,
    val dataNascimento: String?,
    val sexo: String?
)