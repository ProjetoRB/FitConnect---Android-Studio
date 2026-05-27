package com.fitconnect.network

import com.fitconnect.model.Consulta
import com.fitconnect.model.LoginRequest
import com.fitconnect.model.LoginResponse
import com.fitconnect.model.Profissional
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // ─────────────────────────────────────────
    // AUTH
    // ─────────────────────────────────────────
    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    // ─────────────────────────────────────────
    // PROFISSIONAIS
    // ─────────────────────────────────────────
    @GET("profissionais")
    fun getProfissionais(): Call<List<Profissional>>

    // ─────────────────────────────────────────
    // CONSULTAS
    // ─────────────────────────────────────────
    @GET("agenda-profissional/aluno/{id}")
    fun getConsultasAluno(
        @Path("id") alunoId: Long
    ): Call<List<Consulta>>

    @PUT("agenda-profissional/status")
    fun cancelarConsulta(
        @Body body: Map<String, Any>
    ): Call<Unit>
}