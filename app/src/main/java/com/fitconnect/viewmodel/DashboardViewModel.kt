package com.fitconnect.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitconnect.model.Consulta
import com.fitconnect.model.Profissional
import com.fitconnect.network.ApiClient
import com.fitconnect.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class DashboardState {
    data class ProfissionaisCarregados(val profissionais: List<Profissional>) : DashboardState()
    data class ConsultasCarregadas(val consultas: List<Consulta>) : DashboardState()
    data class Erro(val mensagem: String) : DashboardState()
    object Loading : DashboardState()
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _dashboardState = MutableLiveData<DashboardState>()
    val dashboardState: LiveData<DashboardState> = _dashboardState

    private val api = ApiClient.retrofit.create(ApiService::class.java)

    // Pega o id do aluno logado via SharedPreferences
    private val alunoId: Long
        get() {
            val prefs = getApplication<Application>()
                .getSharedPreferences("fitconnect", android.content.Context.MODE_PRIVATE)
            return prefs.getLong("usuarioId", 1L)
        }

    // ─────────────────────────────────────────
    // CARREGAR PROFISSIONAIS
    // ─────────────────────────────────────────
    fun carregarProfissionais(filtro: String = "todos") {
        _dashboardState.value = DashboardState.Loading

        api.getProfissionais().enqueue(object : Callback<List<Profissional>> {
            override fun onResponse(
                call: Call<List<Profissional>>,
                response: Response<List<Profissional>>
            ) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    val filtrado = if (filtro == "todos") {
                        lista
                    } else {
                        lista.filter { it.areaProfissional == filtro }
                    }
                    _dashboardState.value = DashboardState.ProfissionaisCarregados(filtrado)
                } else {
                    _dashboardState.value = DashboardState.Erro("Erro ao carregar profissionais.")
                }
            }

            override fun onFailure(call: Call<List<Profissional>>, t: Throwable) {
                _dashboardState.value = DashboardState.Erro("Erro de conexão: ${t.message}")
            }
        })
    }

    // ─────────────────────────────────────────
    // CARREGAR CONSULTAS PENDENTES
    // ─────────────────────────────────────────
    fun carregarConsultasPendentes() {
        api.getConsultasAluno(alunoId).enqueue(object : Callback<List<Consulta>> {
            override fun onResponse(
                call: Call<List<Consulta>>,
                response: Response<List<Consulta>>
            ) {
                if (response.isSuccessful) {
                    val ativas = (response.body() ?: emptyList())
                        .filter { it.statusHorario == "agendado" }
                    _dashboardState.value = DashboardState.ConsultasCarregadas(ativas)
                } else {
                    _dashboardState.value = DashboardState.Erro("Erro ao carregar consultas.")
                }
            }

            override fun onFailure(call: Call<List<Consulta>>, t: Throwable) {
                _dashboardState.value = DashboardState.Erro("Erro de conexão: ${t.message}")
            }
        })
    }

    // ─────────────────────────────────────────
    // CANCELAR CONSULTA
    // ─────────────────────────────────────────
    fun cancelarConsulta(horarioId: Long) {
        val body = mapOf<String, Any>(
            "horarioId" to horarioId,
            "status" to "cancelado_aluno"
        )

        api.cancelarConsulta(body).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    carregarConsultasPendentes()
                } else {
                    _dashboardState.value = DashboardState.Erro("Erro ao cancelar consulta.")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                _dashboardState.value = DashboardState.Erro("Erro de conexão: ${t.message}")
            }
        })
    }
}