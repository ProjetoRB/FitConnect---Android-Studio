package com.fitconnect.model

data class Aluno(
    val id: Long,
    val nomeCompleto: String?,
    val dataNascimento: String?,
    val email: String?,
    val cpf: String?,
    val sexo: String?,
    val peso: String?,
    val altura: String?
)