package com.fitconnect

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ContatoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contato)

        findViewById<Button>(R.id.btnVoltarContato).setOnClickListener {
            finish()
        }
    }
}