package com.fitconnect.model

data class LoginResponse(
    val id: Long?,
    val nome: String?,
    val email: String?,
    val tipo: String?,
    val mensagem: String?
)