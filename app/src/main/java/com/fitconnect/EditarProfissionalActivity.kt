package com.fitconnect

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fitconnect.model.Profissional
import com.fitconnect.model.ProfissionalRequest
import com.fitconnect.network.ApiClient
import com.fitconnect.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarProfissionalActivity : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private lateinit var edtDataNascimento: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtCpf: EditText
    private lateinit var spinnerSexo: Spinner
    private lateinit var spinnerArea: Spinner
    private lateinit var edtDocumento: EditText
    private lateinit var btnSalvar: Button

    private val sexos = arrayOf(
        "Masculino",
        "Feminino"
    )

    private val areas = arrayOf(
        "Nutricionista",
        "Personal Trainer",
        "Fisioterapeuta"
    )

    private val api = ApiClient.retrofit.create(ApiService::class.java)

    private val profissionalId: Long
        get() = getSharedPreferences("fitconnect", MODE_PRIVATE)
            .getLong("usuarioId", 0L)

    private var profissionalAtual: Profissional? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_profissional)

        edtNome = findViewById(R.id.edtNomeProfissional)
        edtDataNascimento = findViewById(R.id.edtDataNascimentoProfissional)
        edtEmail = findViewById(R.id.edtEmailProfissional)
        edtCpf = findViewById(R.id.edtCpfProfissional)
        spinnerSexo = findViewById(R.id.spinnerSexoProfissional)
        spinnerArea = findViewById(R.id.spinnerAreaProfissional)
        edtDocumento = findViewById(R.id.edtDocumentoProfissional)
        btnSalvar = findViewById(R.id.btnSalvarProfissional)

        configurarSpinners()
        buscarProfissional()

        btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun configurarSpinners() {
        val adapterSexo = ArrayAdapter(
            this,
            R.layout.item_spinner,
            sexos
        )

        adapterSexo.setDropDownViewResource(
            R.layout.item_spinner_dropdown
        )

        spinnerSexo.adapter = adapterSexo

        val adapterArea = ArrayAdapter(
            this,
            R.layout.item_spinner,
            areas
        )

        adapterArea.setDropDownViewResource(
            R.layout.item_spinner_dropdown
        )

        spinnerArea.adapter = adapterArea
    }

    private fun buscarProfissional() {
        if (profissionalId == 0L) {
            Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        api.getProfissionalPorId(profissionalId).enqueue(object : Callback<Profissional> {
            override fun onResponse(
                call: Call<Profissional>,
                response: Response<Profissional>
            ) {
                if (response.isSuccessful) {
                    profissionalAtual = response.body()

                    edtNome.setText(profissionalAtual?.nomeCompleto ?: "")
                    edtDataNascimento.setText(profissionalAtual?.dataNascimento ?: "")
                    edtEmail.setText(profissionalAtual?.email ?: "")
                    edtCpf.setText(profissionalAtual?.cpf ?: "")
                    edtDocumento.setText(profissionalAtual?.documentoProfissional ?: "")

                    val posicaoSexo = sexos.indexOf(profissionalAtual?.sexo)
                    if (posicaoSexo >= 0) {
                        spinnerSexo.setSelection(posicaoSexo)
                    }

                    val posicaoArea = areas.indexOf(profissionalAtual?.areaProfissional)
                    if (posicaoArea >= 0) {
                        spinnerArea.setSelection(posicaoArea)
                    }

                } else {
                    Toast.makeText(
                        this@EditarProfissionalActivity,
                        "Erro ao carregar dados.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Profissional>, t: Throwable) {
                Toast.makeText(
                    this@EditarProfissionalActivity,
                    "Erro: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun salvarAlteracoes() {
        val request = ProfissionalRequest(
            nomeCompleto = edtNome.text.toString(),
            dataNascimento = edtDataNascimento.text.toString(),
            email = edtEmail.text.toString(),
            cpf = profissionalAtual?.cpf,
            sexo = spinnerSexo.selectedItem.toString(),
            areaProfissional = spinnerArea.selectedItem.toString(),
            documentoProfissional = edtDocumento.text.toString(),
            senha = null
        )

        api.atualizarProfissional(profissionalId, request)
            .enqueue(object : Callback<Profissional> {
                override fun onResponse(
                    call: Call<Profissional>,
                    response: Response<Profissional>
                ) {
                    if (response.isSuccessful) {
                        val profissionalAtualizado = response.body()

                        getSharedPreferences("fitconnect", MODE_PRIVATE)
                            .edit()
                            .putString("usuarioNome", profissionalAtualizado?.nomeCompleto)
                            .apply()

                        Toast.makeText(
                            this@EditarProfissionalActivity,
                            "Perfil atualizado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    } else {
                        Toast.makeText(
                            this@EditarProfissionalActivity,
                            "Erro ao atualizar perfil.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Profissional>, t: Throwable) {
                    Toast.makeText(
                        this@EditarProfissionalActivity,
                        "Erro: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}