package com.fitconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fitconnect.model.Aluno
import com.fitconnect.network.ApiClient
import com.fitconnect.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MinhaContaAlunoActivity : AppCompatActivity() {

    private val api = ApiClient.retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minha_conta_aluno)

        val btnEditarPerfil = findViewById<Button>(R.id.btnEditarPerfil)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        btnEditarPerfil.setOnClickListener {
            startActivity(
                Intent(this, EditarAlunoActivity::class.java)
            )
        }

        btnVoltar.setOnClickListener {
            finish()
        }

        carregarDadosAluno()
    }

    override fun onResume() {
        super.onResume()
        carregarDadosAluno()
    }

    private fun carregarDadosAluno() {

        val prefs = getSharedPreferences(
            "fitconnect",
            MODE_PRIVATE
        )

        val alunoId = prefs.getLong("usuarioId", 0L)

        api.getAlunoPorId(alunoId)
            .enqueue(object : Callback<Aluno> {

                override fun onResponse(
                    call: Call<Aluno>,
                    response: Response<Aluno>
                ) {

                    if (response.isSuccessful) {

                        val aluno = response.body()

                        findViewById<TextView>(R.id.txtNomeAluno)
                            .text = aluno?.nomeCompleto ?: "--"

                        findViewById<TextView>(R.id.txtDataNascimentoAluno)
                            .text = aluno?.dataNascimento ?: "--"

                        findViewById<TextView>(R.id.txtEmailAluno)
                            .text = aluno?.email ?: "--"

                        findViewById<TextView>(R.id.txtCpfAluno)
                            .text = aluno?.cpf ?: "--"

                        findViewById<TextView>(R.id.txtSexoAluno)
                            .text = aluno?.sexo ?: "--"

                        findViewById<TextView>(R.id.txtPesoAluno)
                            .text = aluno?.peso ?: "--"

                        findViewById<TextView>(R.id.txtAlturaAluno)
                            .text = aluno?.altura ?: "--"

                        findViewById<TextView>(R.id.txtTipoConta)
                            .text = "Aluno"

                        findViewById<TextView>(R.id.txtIdUsuario)
                            .text = aluno?.id.toString()

                    } else {

                        Toast.makeText(
                            this@MinhaContaAlunoActivity,
                            "Erro ao carregar dados.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<Aluno>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@MinhaContaAlunoActivity,
                        "Erro: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}