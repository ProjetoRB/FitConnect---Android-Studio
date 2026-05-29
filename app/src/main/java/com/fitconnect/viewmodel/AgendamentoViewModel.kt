package com.fitconnect.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitconnect.model.AgendamentoRequest
import com.fitconnect.model.Horario
import com.fitconnect.network.ApiClient
import com.fitconnect.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class AgendamentoState {
    object Loading : AgendamentoState()
    data class HorariosCarregados(val horarios: List<Horario>) : AgendamentoState()
    object AgendamentoSucesso : AgendamentoState()
    data class Erro(val mensagem: String) : AgendamentoState()
}

class AgendamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableLiveData<AgendamentoState>()
    val state: LiveData<AgendamentoState> = _state

    private val api = ApiClient.retrofit.create(ApiService::class.java)

    private val alunoId: Long
        get() {
            val prefs = getApplication<Application>()
                .getSharedPreferences("fitconnect", android.content.Context.MODE_PRIVATE)
            return prefs.getLong("usuarioId", 1L)
        }

    fun carregarHorarios(profissionalId: Long) {
        _state.value = AgendamentoState.Loading

        api.getHorariosProfissional(profissionalId).enqueue(object : Callback<List<Horario>> {
            override fun onResponse(
                call: Call<List<Horario>>,
                response: Response<List<Horario>>
            ) {
                if (response.isSuccessful) {
                    val todos = response.body() ?: emptyList()
                    _state.value = AgendamentoState.HorariosCarregados(todos)
                } else {
                    _state.value = AgendamentoState.Erro("Erro ao carregar horarios.")
                }
            }

            override fun onFailure(call: Call<List<Horario>>, t: Throwable) {
                _state.value = AgendamentoState.Erro("Erro de conexao: ${t.message}")
            }
        })
    }

    fun confirmarAgendamento(horarioId: Long) {
        _state.value = AgendamentoState.Loading

        val request = AgendamentoRequest(
            alunoId = alunoId,
            horarioId = horarioId
        )

        api.agendarHorario(request).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    _state.value = AgendamentoState.AgendamentoSucesso
                } else {
                    _state.value = AgendamentoState.Erro("Erro ao agendar. Tente novamente.")
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                _state.value = AgendamentoState.Erro("Erro de conexao: ${t.message}")
            }
        })
    }
}

