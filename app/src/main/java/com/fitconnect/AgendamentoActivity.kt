package com.fitconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fitconnect.databinding.ActivityAgendamentoBinding
import com.fitconnect.model.Horario
import com.fitconnect.viewmodel.AgendamentoState
import com.fitconnect.viewmodel.AgendamentoViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.DayOfWeek

class AgendamentoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgendamentoBinding
    private val viewModel: AgendamentoViewModel by viewModels()

    private var profissionalId: Long = 0
    private var horarioSelecionadoId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_agendamento)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Receber dados do profissional
        profissionalId = intent.getLongExtra("profId", 0)
        val nome = intent.getStringExtra("profNome") ?: "--"
        val area = intent.getStringExtra("profArea") ?: "--"

        binding.tvNomeProfissional.text = nome
        binding.tvAreaProfissional.text = area

        configurarBotoes()
        observarEstado()

        viewModel.carregarHorarios(profissionalId)
    }

    private fun configurarBotoes() {
        binding.btnVoltar.setOnClickListener { finish() }

        binding.btnConfirmar.setOnClickListener {
            horarioSelecionadoId?.let { id ->
                viewModel.confirmarAgendamento(id)
            }
        }
    }

    private fun observarEstado() {
        viewModel.state.observe(this) { state ->
            when (state) {

                is AgendamentoState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.listaHorarios.visibility = View.GONE
                    binding.tvSemHorarios.visibility = View.GONE
                }

                is AgendamentoState.HorariosCarregados -> {
                    binding.progressBar.visibility = View.GONE

                    if (state.horarios.isEmpty()) {
                        binding.tvSemHorarios.visibility = View.VISIBLE
                        binding.listaHorarios.visibility = View.GONE
                    } else {
                        binding.tvSemHorarios.visibility = View.GONE
                        binding.listaHorarios.visibility = View.VISIBLE
                        exibirHorarios(state.horarios)
                    }
                }

                is AgendamentoState.AgendamentoSucesso -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Agendamento realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }

                is AgendamentoState.Erro -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.mensagem, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun exibirHorarios(horarios: List<Horario>) {
        val container = binding.listaHorarios
        container.removeAllViews()

        horarios.forEach { horario ->
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_horario, container, false)

            // Formatar data
            val dataFormatada = formatarData(horario.dataDisponivel)
            val diaSemana = obterDiaSemana(horario.dataDisponivel)
            val hora = horario.horaDisponivel.substring(0, 5)

            itemView.findViewById<TextView>(R.id.tvData).text = dataFormatada
            itemView.findViewById<TextView>(R.id.tvDiaSemana).text = diaSemana
            itemView.findViewById<TextView>(R.id.tvHora).text = hora

            // Selecionar horário ao clicar
            itemView.setOnClickListener {
                // Resetar todos
                resetarSelecao(container)

                // Marcar este como selecionado
                itemView.setBackgroundResource(R.drawable.bg_horario_selecionado)
                horarioSelecionadoId = horario.id

                // Habilitar botão confirmar
                binding.btnConfirmar.isEnabled = true
                binding.btnConfirmar.alpha = 1.0f
            }

            container.addView(itemView)
        }
    }

    private fun resetarSelecao(container: LinearLayout) {
        for (i in 0 until container.childCount) {
            container.getChildAt(i)
                .setBackgroundResource(R.drawable.bg_horario_normal)
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

    private fun obterDiaSemana(data: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(data, formatter)
            when (date.dayOfWeek) {
                DayOfWeek.MONDAY    -> "Segunda-feira"
                DayOfWeek.TUESDAY   -> "Terca-feira"
                DayOfWeek.WEDNESDAY -> "Quarta-feira"
                DayOfWeek.THURSDAY  -> "Quinta-feira"
                DayOfWeek.FRIDAY    -> "Sexta-feira"
                DayOfWeek.SATURDAY  -> "Sabado"
                DayOfWeek.SUNDAY    -> "Domingo"
                else -> ""
            }
        } catch (e: Exception) {
            ""
        }
    }
}