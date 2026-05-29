package com.fitconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fitconnect.model.Profissional
import com.fitconnect.network.ApiClient
import com.fitconnect.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MinhaContaProfissionalActivity : AppCompatActivity() {

    private val api = ApiClient.retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minha_conta_profissional)

        val btnEditarPerfil = findViewById<Button>(R.id.btnEditarPerfilProfissional)
        val btnVoltar = findViewById<Button>(R.id.btnVoltarProfissional)

        btnEditarPerfil.setOnClickListener {
            startActivity(
                Intent(this, EditarProfissionalActivity::class.java)
            )
        }

        btnVoltar.setOnClickListener {
            finish()
        }

        carregarDadosProfissional()
    }

    override fun onResume() {
        super.onResume()
        carregarDadosProfissional()
    }

    private fun carregarDadosProfissional() {

        val prefs = getSharedPreferences(
            "fitconnect",
            MODE_PRIVATE
        )

        val profissionalId = prefs.getLong("usuarioId", 0L)

        api.getProfissionalPorId(profissionalId)
            .enqueue(object : Callback<Profissional> {

                override fun onResponse(
                    call: Call<Profissional>,
                    response: Response<Profissional>
                ) {

                    if (response.isSuccessful) {

                        val profissional = response.body()

                        findViewById<TextView>(R.id.txtNomeProfissional)
                            .text = profissional?.nomeCompleto ?: "--"

                        findViewById<TextView>(R.id.txtDataNascimentoProfissional)
                            .text = profissional?.dataNascimento ?: "--"

                        findViewById<TextView>(R.id.txtEmailProfissional)
                            .text = profissional?.email ?: "--"

                        findViewById<TextView>(R.id.txtCpfProfissional)
                            .text = profissional?.cpf ?: "--"

                        findViewById<TextView>(R.id.txtSexoProfissional)
                            .text = profissional?.sexo ?: "--"

                        findViewById<TextView>(R.id.txtAreaProfissional)
                            .text = profissional?.areaProfissional ?: "--"

                        findViewById<TextView>(R.id.txtDocumentoProfissional)
                            .text = profissional?.documentoProfissional ?: "--"

                        findViewById<TextView>(R.id.txtTipoContaProfissional)
                            .text = "Profissional"

                        findViewById<TextView>(R.id.txtIdUsuarioProfissional)
                            .text = profissional?.id.toString()

                    } else {

                        Toast.makeText(
                            this@MinhaContaProfissionalActivity,
                            "Erro ao carregar dados.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<Profissional>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@MinhaContaProfissionalActivity,
                        "Erro: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}