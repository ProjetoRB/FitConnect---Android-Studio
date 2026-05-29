package com.fitconnect.network

import com.fitconnect.model.ProfissionalRequest
import com.fitconnect.model.Aluno
import com.fitconnect.model.AlunoRequest
import com.fitconnect.model.AgendamentoRequest
import com.fitconnect.model.Consulta
import com.fitconnect.model.Horario
import com.fitconnect.model.LoginRequest
import com.fitconnect.model.LoginResponse
import com.fitconnect.model.Profissional
import com.fitconnect.model.AtualizarStatusAgendaRequest
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
    // HORÁRIOS
    // ─────────────────────────────────────────
    @GET("agenda-profissional/profissional/{id}")
    fun getHorariosProfissional(
        @Path("id") profissionalId: Long
    ): Call<List<Horario>>

    // ─────────────────────────────────────────
    // AGENDAMENTO
    // ─────────────────────────────────────────
    @PUT("agenda-profissional/agendar")
    fun agendarHorario(
        @Body request: AgendamentoRequest
    ): Call<Any>

    // ─────────────────────────────────────────
    // CONSULTAS
    // ─────────────────────────────────────────
    @GET("agenda-profissional/aluno/{id}")
    fun getConsultasAluno(
        @Path("id") alunoId: Long
    ): Call<List<Consulta>>

    @GET("agenda-profissional/profissional/{id}/todos")
    fun getConsultasProfissional(
        @Path("id") profissionalId: Long
    ): Call<List<Consulta>>

    @PUT("agenda-profissional/status")
    fun cancelarConsulta(
        @Body request: AtualizarStatusAgendaRequest
    ): Call<Unit>

    // ─────────────────────────────────────────
// ALUNOS
// ─────────────────────────────────────────
    @GET("alunos/{id}")
    fun getAlunoPorId(
        @Path("id") alunoId: Long
    ): Call<Aluno>

    @PUT("alunos/{id}")
    fun atualizarAluno(
        @Path("id") alunoId: Long,
        @Body request: AlunoRequest
    ): Call<Aluno>

    @GET("profissionais/{id}")
    fun getProfissionalPorId(
        @Path("id") id: Long
    ): Call<Profissional>

    @PUT("profissionais/{id}")
    fun atualizarProfissional(
        @Path("id") id: Long,
        @Body profissional: ProfissionalRequest
    ): Call<Profissional>

}