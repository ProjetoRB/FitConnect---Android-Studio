package com.fitconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fitconnect.model.LoginRequest
import com.fitconnect.model.LoginResponse
import com.fitconnect.network.ApiClient
import com.fitconnect.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCriarConta = findViewById<Button>(R.id.btnCriarConta)

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val senha = edtSenha.text.toString()

            val api = ApiClient.retrofit.create(ApiService::class.java)

            api.login(LoginRequest(email, senha)).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val usuario = response.body()

                        getSharedPreferences("fitconnect", MODE_PRIVATE)
                            .edit()
                            .putLong("usuarioId", usuario?.id ?: 0L)
                            .putString("usuarioNome", usuario?.nome)
                            .putString("usuarioTipo", usuario?.tipo)
                            .apply()

                        Toast.makeText(
                            this@LoginActivity,
                            "Bem-vindo ${usuario?.nome}",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (usuario?.tipo == "Aluno") {
                            startActivity(Intent(this@LoginActivity, DashboardAlunoActivity::class.java))
                        } else {
                            startActivity(Intent(this@LoginActivity, DashboardProfissionalActivity::class.java))
                        }

                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Email ou senha inválidos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Erro: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        btnCriarConta.setOnClickListener {
            val opcoes = arrayOf("Aluno", "Profissional")

            AlertDialog.Builder(this)
                .setTitle("Escolha o tipo de conta")
                .setItems(opcoes) { _, which ->
                    if (which == 0) {
                        startActivity(Intent(this, CadastroAlunoActivity::class.java))
                    } else {
                        startActivity(Intent(this, CadastroProfissionalActivity::class.java))
                    }
                }
                .show()
        }
    }
}