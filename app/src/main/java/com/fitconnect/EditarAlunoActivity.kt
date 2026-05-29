package com.fitconnect

import android.widget.Spinner
import android.widget.ArrayAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fitconnect.model.Aluno
import com.fitconnect.model.AlunoRequest
import com.fitconnect.network.ApiClient
import com.fitconnect.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarAlunoActivity : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private lateinit var edtDataNascimento: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtCpf: EditText

    private lateinit var spinnerSexo: Spinner
    private lateinit var edtPeso: EditText
    private lateinit var edtAltura: EditText
    private lateinit var btnSalvar: Button

    private val sexos = arrayOf(
        "Masculino",
        "Feminino"
    )

    private val api = ApiClient.retrofit.create(ApiService::class.java)

    private val alunoId: Long
        get() = getSharedPreferences("fitconnect", MODE_PRIVATE)
            .getLong("usuarioId", 0L)

    private var alunoAtual: Aluno? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_aluno)

        edtNome = findViewById(R.id.edtNome)
        edtDataNascimento = findViewById(R.id.edtDataNascimento)
        edtEmail = findViewById(R.id.edtEmail)
        edtCpf = findViewById(R.id.edtCpf)
        spinnerSexo = findViewById(R.id.spinnerSexo)

        val adapterSexo = ArrayAdapter(
            this,
            R.layout.item_spinner,
            sexos
        )

        adapterSexo.setDropDownViewResource(
            R.layout.item_spinner_dropdown
        )

        spinnerSexo.adapter = adapterSexo

        spinnerSexo.adapter = adapterSexo

        spinnerSexo.adapter = adapterSexo

        spinnerSexo.adapter = adapterSexo
        edtPeso = findViewById(R.id.edtPeso)
        edtAltura = findViewById(R.id.edtAltura)
        btnSalvar = findViewById(R.id.btnSalvar)

        buscarAluno()

        btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun buscarAluno() {
        if (alunoId == 0L) {
            Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        api.getAlunoPorId(alunoId).enqueue(object : Callback<Aluno> {
            override fun onResponse(
                call: Call<Aluno>,
                response: Response<Aluno>
            ) {
                if (response.isSuccessful) {
                    alunoAtual = response.body()

                    edtNome.setText(alunoAtual?.nomeCompleto ?: "")
                    edtDataNascimento.setText(alunoAtual?.dataNascimento ?: "")
                    edtEmail.setText(alunoAtual?.email ?: "")
                    edtCpf.setText(alunoAtual?.cpf ?: "")
                    val posicaoSexo =
                        sexos.indexOf(alunoAtual?.sexo)

                    if (posicaoSexo >= 0) {
                        spinnerSexo.setSelection(posicaoSexo)
                    }
                    edtPeso.setText(alunoAtual?.peso ?: "")
                    edtAltura.setText(alunoAtual?.altura ?: "")
                } else {
                    Toast.makeText(
                        this@EditarAlunoActivity,
                        "Erro ao carregar dados.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Aluno>, t: Throwable) {
                Toast.makeText(
                    this@EditarAlunoActivity,
                    "Erro: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun salvarAlteracoes() {
        val request = AlunoRequest(
            nomeCompleto = edtNome.text.toString(),
            dataNascimento = edtDataNascimento.text.toString(),
            email = edtEmail.text.toString(),
            cpf = alunoAtual?.cpf,
            peso = edtPeso.text.toString(),
            altura = edtAltura.text.toString(),
            sexo = spinnerSexo.selectedItem.toString(),
            senha = null
        )

        api.atualizarAluno(alunoId, request).enqueue(object : Callback<Aluno> {
            override fun onResponse(
                call: Call<Aluno>,
                response: Response<Aluno>
            ) {
                if (response.isSuccessful) {
                    val alunoAtualizado = response.body()

                    getSharedPreferences("fitconnect", MODE_PRIVATE)
                        .edit()
                        .putString("usuarioNome", alunoAtualizado?.nomeCompleto)
                        .apply()

                    Toast.makeText(
                        this@EditarAlunoActivity,
                        "Perfil atualizado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                } else {
                    Toast.makeText(
                        this@EditarAlunoActivity,
                        "Erro ao atualizar perfil.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Aluno>, t: Throwable) {
                Toast.makeText(
                    this@EditarAlunoActivity,
                    "Erro: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}