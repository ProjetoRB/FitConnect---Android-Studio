package com.fitconnect

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.fitconnect.databinding.ActivityDashboardAlunoBinding
import com.fitconnect.model.AtualizarStatusAgendaRequest
import com.fitconnect.model.Consulta
import com.fitconnect.model.Profissional
import com.fitconnect.network.ApiClient
import com.fitconnect.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardAlunoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAlunoBinding
    private var listaProfissionais: List<Profissional> = emptyList()
    private val api = ApiClient.retrofit.create(ApiService::class.java)

    private val alunoId: Long
        get() = getSharedPreferences("fitconnect", MODE_PRIVATE)
            .getLong("usuarioId", 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardAlunoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.btnMinhaConta.setOnClickListener {
            startActivity(Intent(this, MinhaContaAlunoActivity::class.java))
        }

        binding.btnContato.setOnClickListener {
            startActivity(Intent(this, ContatoActivity::class.java))
        }

        binding.btnServicos.setOnClickListener {
            startActivity(Intent(this, ServicosActivity::class.java))
        }

        binding.btnSairAluno.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnFiltroTodos.setOnClickListener {
            mostrarProfissionais(listaProfissionais)
        }

        binding.btnFiltroNutri.setOnClickListener {
            filtrarProfissionais("Nutricionista")
        }

        binding.btnFiltroPersonal.setOnClickListener {
            filtrarProfissionais("Personal Trainer")
        }

        binding.btnFiltroFisio.setOnClickListener {
            filtrarProfissionais("Fisioterapeuta")
        }

        buscarProfissionais()
        buscarConsultasPendentes()
    }

    override fun onResume() {
        super.onResume()
        buscarConsultasPendentes()
    }

    private fun buscarProfissionais() {
        api.getProfissionais().enqueue(object : Callback<List<Profissional>> {
            override fun onResponse(
                call: Call<List<Profissional>>,
                response: Response<List<Profissional>>
            ) {
                if (response.isSuccessful) {
                    listaProfissionais = response.body() ?: emptyList()
                    mostrarProfissionais(listaProfissionais)
                } else {
                    binding.tvBuscando.text = "Erro ao buscar profissionais."
                }
            }

            override fun onFailure(call: Call<List<Profissional>>, t: Throwable) {
                binding.tvBuscando.text = "Falha na conexão: ${t.message}"
            }
        })
    }

    private fun buscarConsultasPendentes() {
        if (alunoId == 0L) return

        api.getConsultasAluno(alunoId).enqueue(object : Callback<List<Consulta>> {
            override fun onResponse(
                call: Call<List<Consulta>>,
                response: Response<List<Consulta>>
            ) {
                if (response.isSuccessful) {
                    val consultas = response.body()
                        ?.filter { it.statusHorario.equals("agendado", ignoreCase = true) }
                        ?: emptyList()

                    mostrarConsultas(consultas)
                }
            }

            override fun onFailure(call: Call<List<Consulta>>, t: Throwable) {
                Toast.makeText(
                    this@DashboardAlunoActivity,
                    "Erro ao buscar consultas: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun mostrarConsultas(consultas: List<Consulta>) {
        binding.pendingList.removeAllViews()

        if (consultas.isEmpty()) {
            val vazio = TextView(this)
            vazio.text = "Nenhuma consulta pendente."
            vazio.setTextColor(Color.parseColor("#64748B"))
            vazio.textSize = 14f
            binding.pendingList.addView(vazio)
            return
        }

        consultas.forEach { consulta ->
            val card = LinearLayout(this)
            card.orientation = LinearLayout.VERTICAL
            card.setPadding(24, 20, 24, 20)
            card.setBackgroundColor(Color.parseColor("#1E293B"))

            val texto = TextView(this)
            texto.text = """
                Consulta agendada
                Data: ${consulta.dataDisponivel}
                Hora: ${consulta.horaDisponivel}
            """.trimIndent()
            texto.setTextColor(Color.WHITE)
            texto.textSize = 16f

            val btnCancelar = Button(this)
            btnCancelar.text = "Cancelar consulta"
            btnCancelar.setTextColor(Color.WHITE)
            btnCancelar.setBackgroundColor(Color.parseColor("#EF4444"))

            btnCancelar.setOnClickListener {
                cancelarConsulta(consulta.id)
            }

            card.addView(texto)
            card.addView(btnCancelar)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 16)
            card.layoutParams = params

            binding.pendingList.addView(card)
        }
    }

    private fun cancelarConsulta(horarioId: Long) {
        val body = AtualizarStatusAgendaRequest(
            horarioId = horarioId,
            alunoId = alunoId,
            status = "cancelado_aluno"
        )

        api.cancelarConsulta(body).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@DashboardAlunoActivity,
                        "Consulta cancelada com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    buscarConsultasPendentes()
                    buscarProfissionais()
                } else {
                    Toast.makeText(
                        this@DashboardAlunoActivity,
                        "Erro ao cancelar consulta.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(
                    this@DashboardAlunoActivity,
                    "Erro: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filtrarProfissionais(area: String) {
        val filtrados = listaProfissionais.filter {
            it.areaProfissional?.equals(area, ignoreCase = true) == true
        }

        mostrarProfissionais(filtrados)
    }

    private fun mostrarProfissionais(profissionais: List<Profissional>) {
        binding.profissionaisContainer.removeAllViews()

        if (profissionais.isEmpty()) {
            val vazio = TextView(this)
            vazio.text = "Nenhum profissional encontrado."
            vazio.setTextColor(Color.parseColor("#64748B"))
            vazio.textSize = 14f
            binding.profissionaisContainer.addView(vazio)
            return
        }

        profissionais.forEach { profissional ->
            val card = LinearLayout(this)
            card.orientation = LinearLayout.VERTICAL
            card.setPadding(24, 20, 24, 20)
            card.setBackgroundColor(Color.parseColor("#1E293B"))

            val texto = TextView(this)
            texto.text = """
                ${profissional.nomeCompleto}
                ${profissional.areaProfissional}
            """.trimIndent()
            texto.setTextColor(Color.WHITE)
            texto.textSize = 16f

            val btnAgendar = Button(this)
            btnAgendar.text = "Agendar"
            btnAgendar.setTextColor(Color.WHITE)
            btnAgendar.setBackgroundColor(Color.parseColor("#2563EB"))

            btnAgendar.setOnClickListener {
                val intent = Intent(this, AgendamentoActivity::class.java)
                intent.putExtra("profId", profissional.id)
                intent.putExtra("profNome", profissional.nomeCompleto)
                intent.putExtra("profArea", profissional.areaProfissional)
                startActivity(intent)
            }

            card.addView(texto)
            card.addView(btnAgendar)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 16)
            card.layoutParams = params

            binding.profissionaisContainer.addView(card)
        }
    }
}