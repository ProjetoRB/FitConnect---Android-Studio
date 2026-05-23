package com.fitconnect

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class CadastroProfissionalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_profissional)

        configurarSpinnerSexo()
        configurarSpinnerProfissao()
    }

    private fun configurarSpinnerSexo() {

        val spinnerSexo =
            findViewById<Spinner>(R.id.spinnerSexoProfissional)

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

    private fun configurarSpinnerProfissao() {

        val spinnerProfissao =
            findViewById<Spinner>(R.id.spinnerAreaProfissional)

        val opcoes = arrayOf(
            "Selecione a profissão",
            "Nutricionista",
            "Personal Trainer",
            "Fisioterapeuta"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            opcoes
        )

        spinnerProfissao.adapter = adapter
    }
}