package com.fitconnect

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class CadastroAlunoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_aluno)

        configurarSpinnerSexo()
    }

    private fun configurarSpinnerSexo() {

        val spinnerSexo =
            findViewById<Spinner>(R.id.spinnerSexoAluno)

        val opcoes = arrayOf(
            "Selecione o sexo",
            "Masculino",
            "Feminino"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            opcoes
        )

        spinnerSexo.adapter = adapter
    }
}