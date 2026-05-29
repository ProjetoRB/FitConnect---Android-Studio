package com.fitconnect.model

data class AlunoRequest(
    val nomeCompleto: String?,
    val dataNascimento: String?,
    val email: String?,
    val cpf: String?,
    val peso: String?,
    val altura: String?,
    val sexo: String?,
    val senha: String?
)