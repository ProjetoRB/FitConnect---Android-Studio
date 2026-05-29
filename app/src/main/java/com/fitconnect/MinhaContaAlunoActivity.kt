package com.fitconnect

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MinhaContaAlunoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minha_conta_aluno)

        val txtNome = findViewById<TextView>(R.id.txtNomeAluno)
        val txtTipo = findViewById<TextView>(R.id.txtTipoConta)
        val txtId = findViewById<TextView>(R.id.txtIdUsuario)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        val prefs = getSharedPreferences(
            "fitconnect",
            MODE_PRIVATE
        )

        txtNome.text =
            prefs.getString("usuarioNome", "Não encontrado")

        txtTipo.text =
            prefs.getString("usuarioTipo", "Aluno")

        txtId.text =
            prefs.getLong("usuarioId", 0L).toString()

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}