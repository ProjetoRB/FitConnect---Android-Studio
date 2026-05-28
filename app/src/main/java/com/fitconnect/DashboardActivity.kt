package com.fitconnect

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fitconnect.databinding.ActivityDashboardAlunoBinding
import com.fitconnect.viewmodel.DashboardState
import com.fitconnect.viewmodel.DashboardViewModel

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAlunoBinding
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard_aluno)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        configurarAvatar()
        configurarFiltros()
        observarEstado()

        viewModel.carregarProfissionais()
        viewModel.carregarConsultasPendentes()
    }

    private fun configurarAvatar() {
        val prefs = getSharedPreferences("fitconnect", MODE_PRIVATE)
        val nome = prefs.getString("usuarioNome", "") ?: ""

        if (nome.isNotBlank()) {
            val iniciais = nome.trim().split(" ")
                .take(2)
                .map { it.first().uppercaseChar() }
                .joinToString("")
        }
    }

    private fun configurarFiltros() {
        val botoes = mapOf(
            binding.btnFiltroTodos    to "todos",
            binding.btnFiltroNutri    to "Nutricionista",
            binding.btnFiltroPersonal to "Personal Trainer",
            binding.btnFiltroFisio    to "Fisioterapeuta"
        )

        botoes.forEach { (btn, filtro) ->
            btn.setOnClickListener {
                botoes.keys.forEach { b ->
                    b.setBackgroundResource(R.drawable.bg_filter_inactive)
                    b.setTextColor(getColor(R.color.cinza_texto))
                }
                btn.setBackgroundResource(R.drawable.bg_filter_active)
                btn.setTextColor(getColor(R.color.verde_escuro))
                viewModel.carregarProfissionais(filtro)
            }
        }
    }

    private fun observarEstado() {
        viewModel.dashboardState.observe(this) { state ->
            when (state) {
                is DashboardState.ProfissionaisCarregados -> exibirProfissionais(state.profissionais)
                is DashboardState.ConsultasCarregadas -> exibirConsultas(state.consultas)
                is DashboardState.Erro -> Toast.makeText(this, state.mensagem, Toast.LENGTH_SHORT).show()
                else -> Unit
            }
        }
    }

    private fun exibirProfissionais(profissionais: List<com.fitconnect.model.Profissional>) {
        val container = binding.profissionaisContainer
        container.removeAllViews()

        if (profissionais.isEmpty()) {
            val tv = TextView(this)
            tv.text = "Nenhum profissional encontrado."
            tv.setTextColor(getColor(android.R.color.darker_gray))
            container.addView(tv)
            return
        }

        profissionais.forEach { prof ->
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_profissional, container, false)

            itemView.findViewById<TextView>(R.id.tvNomeProfissional).text = prof.nomeCompleto
            itemView.findViewById<TextView>(R.id.tvAreaProfissional).text = prof.areaProfissional

            itemView.findViewById<Button>(R.id.btnConversar).setOnClickListener {
                Toast.makeText(this, "Chat em breve!", Toast.LENGTH_SHORT).show()
            }

            // Navegar para AgendamentoActivity
            itemView.findViewById<Button>(R.id.btnAgendar).setOnClickListener {
                intent.putExtra("profId", prof.id)
                intent.putExtra("profNome", prof.nomeCompleto)
                intent.putExtra("profArea", prof.areaProfissional)
                startActivity(intent)
            }

            container.addView(itemView)
        }
    }

    private fun exibirConsultas(consultas: List<com.fitconnect.model.Consulta>) {
        val container = binding.pendingList
        container.removeAllViews()

        binding.tvSemConsultas.visibility =
            if (consultas.isEmpty()) View.VISIBLE else View.GONE

        consultas.forEach { consulta ->
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_consulta, container, false)

            itemView.findViewById<TextView>(R.id.tvDataConsulta).text =
                formatarData(consulta.dataDisponivel)
            itemView.findViewById<TextView>(R.id.tvHoraConsulta).text =
                consulta.horaDisponivel.substring(0, 5)
            itemView.findViewById<TextView>(R.id.tvStatusConsulta).text =
                consulta.statusHorario

            itemView.findViewById<Button>(R.id.btnCancelarConsulta).setOnClickListener {
                viewModel.cancelarConsulta(consulta.id)
            }

            container.addView(itemView)
        }
    }

    private fun formatarData(data: String): String {
        return try {
            val partes = data.split("-")
            "${partes[2]}/${partes[1]}/${partes[0]}"
        } catch (e: Exception) {
            data
        }
    }
}