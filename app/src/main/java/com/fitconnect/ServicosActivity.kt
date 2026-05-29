package com.fitconnect

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ServicosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicos)

        findViewById<Button>(R.id.btnVoltarServicos).setOnClickListener {
            finish()
        }
    }
}