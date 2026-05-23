package com.fitconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnCriarConta = findViewById<Button>(R.id.btnCriarConta)

        btnCriarConta.setOnClickListener {

            val opcoes = arrayOf(
                "Aluno",
                "Profissional"
            )

            val builder = AlertDialog.Builder(this)

            builder.setTitle("Escolha o tipo de conta")

            builder.setItems(opcoes) { _, which ->

                if (which == 0) {

                    val intent =
                        Intent(this, CadastroAlunoActivity::class.java)

                    startActivity(intent)

                } else {

                    val intent =
                        Intent(this, CadastroProfissionalActivity::class.java)

                    startActivity(intent)
                }
            }

            builder.show()
        }
    }
}