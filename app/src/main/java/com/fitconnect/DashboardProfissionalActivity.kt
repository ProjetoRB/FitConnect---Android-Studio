package com.fitconnect

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.fitconnect.databinding.ActivityDashboardProfissionalBinding
import com.fitconnect.model.AtualizarStatusAgendaRequest
import com.fitconnect.model.Consulta
import com.fitconnect.network.ApiClient
import com.fitconnect.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardProfissionalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardProfissionalBinding
    private val api = ApiClient.retrofit.create(ApiService::class.java)

    private val profissionalId: Long
        get() = getSharedPreferences("fitconnect", MODE_PRIVATE)
            .getLong("usuarioId", 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardProfissionalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("fitconnect", MODE_PRIVATE)
        val nome = prefs.getString("usuarioNome", "Profissional")
        binding.tvBoasVindasProfissional.text = "Olá, $nome"

        binding.btnRefreshProfissional.setOnClickListener {

            carregarAgendamentos()

            Toast.makeText(
                this,
                "Atualizado com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnMenuProfissional.setOnClickListener {
            binding.drawerLayoutProfissional.openDrawer(GravityCompat.START)
        }

        binding.btnSairProfissional.setOnClickListener {
            confirmarSaida()
        }

        binding.btnOcultarAgendamentos.setOnClickListener {
            alternarAgendamentos()
        }

        carregarAgendamentos()
    }

    override fun onResume() {
        super.onResume()
        carregarAgendamentos()
    }

    private fun confirmarSaida() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Deseja sair da sua conta?")
            .setPositiveButton("Sim") { _, _ ->
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun alternarAgendamentos() {
        if (binding.containerAgendamentosProfissional.visibility == View.VISIBLE) {
            binding.containerAgendamentosProfissional.visibility = View.GONE
            binding.btnOcultarAgendamentos.text = "Mostrar agendamentos"
        } else {
            binding.containerAgendamentosProfissional.visibility = View.VISIBLE
            binding.btnOcultarAgendamentos.text = "Ocultar agendamentos"
        }
    }

    private fun carregarAgendamentos() {
        api.getConsultasProfissional(profissionalId)
            .enqueue(object : Callback<List<Consulta>> {
                override fun onResponse(
                    call: Call<List<Consulta>>,
                    response: Response<List<Consulta>>
                ) {
                    if (response.isSuccessful) {
                        val agendados = response.body()
                            ?.filter { it.statusHorario.equals("agendado", ignoreCase = true) }
                            ?: emptyList()

                        mostrarAgendamentos(agendados)
                    }
                }

                override fun onFailure(call: Call<List<Consulta>>, t: Throwable) {
                    Toast.makeText(
                        this@DashboardProfissionalActivity,
                        "Erro: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun mostrarAgendamentos(consultas: List<Consulta>) {
        binding.containerAgendamentosProfissional.removeAllViews()

        binding.tvTotalAgendamentos.text = consultas.size.toString()
        binding.tvTotalAlunos.text = consultas.mapNotNull { it.alunoId }.distinct().size.toString()

        if (consultas.isEmpty()) {
            val vazio = TextView(this)
            vazio.text = "Nenhum agendamento encontrado"
            vazio.setTextColor(Color.parseColor("#94A3B8"))
            vazio.textSize = 15f
            binding.containerAgendamentosProfissional.addView(vazio)
            return
        }

        consultas.forEach { consulta ->
            val card = LinearLayout(this)
            card.orientation = LinearLayout.VERTICAL
            card.setPadding(24, 20, 24, 20)
            card.setBackgroundColor(Color.parseColor("#1E293B"))

            val texto = TextView(this)
            texto.text = """
                Aluno ID: ${consulta.alunoId ?: "--"}
                Data: ${consulta.dataDisponivel ?: "--"}
                Hora: ${consulta.horaDisponivel ?: "--"}
                Status: ${consulta.statusHorario ?: "--"}
            """.trimIndent()
            texto.setTextColor(Color.WHITE)
            texto.textSize = 16f

            val btnCancelar = Button(this)
            btnCancelar.text = "Cancelar Consulta"
            btnCancelar.setTextColor(Color.WHITE)
            btnCancelar.setBackgroundColor(Color.parseColor("#EF4444"))

            btnCancelar.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Cancelar consulta")
                    .setMessage("Deseja cancelar esta consulta?")
                    .setPositiveButton("Sim") { _, _ ->
                        cancelarConsulta(consulta.id)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            card.addView(texto)
            card.addView(btnCancelar)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 16)
            card.layoutParams = params

            binding.containerAgendamentosProfissional.addView(card)
        }
    }

    private fun cancelarConsulta(horarioId: Long) {
        val request = AtualizarStatusAgendaRequest(
            horarioId = horarioId,
            alunoId = 0L,
            status = "cancelado_profissional"
        )

        api.cancelarConsulta(request).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@DashboardProfissionalActivity,
                        "Consulta cancelada com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    carregarAgendamentos()
                } else {
                    Toast.makeText(
                        this@DashboardProfissionalActivity,
                        "Erro ao cancelar consulta.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(
                    this@DashboardProfissionalActivity,
                    "Erro: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}