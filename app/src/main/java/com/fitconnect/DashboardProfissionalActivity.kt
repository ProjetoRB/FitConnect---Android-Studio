package com.fitconnect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.fitconnect.databinding.ActivityDashboardProfissionalBinding

class DashboardProfissionalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardProfissionalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityDashboardProfissionalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("fitconnect", MODE_PRIVATE)
        val nome = prefs.getString("usuarioNome", "Profissional")
        binding.tvBoasVindasProfissional.text = "Olá, $nome"

        binding.btnMenuProfissional.setOnClickListener {
            binding.drawerLayoutProfissional.openDrawer(GravityCompat.START)
        }

        binding.btnSairProfissional.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}